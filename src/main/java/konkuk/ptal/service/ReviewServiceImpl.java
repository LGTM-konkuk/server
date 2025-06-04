package konkuk.ptal.service;

import konkuk.ptal.domain.enums.ReviewCommentType;
import konkuk.ptal.domain.enums.ReviewRequestStatus;
import konkuk.ptal.dto.request.CreateReviewCommentRequest;
import konkuk.ptal.dto.request.CreateReviewSessionRequest;
import konkuk.ptal.entity.*;
import konkuk.ptal.exception.BadRequestException;
import konkuk.ptal.exception.EntityNotFoundException;
import konkuk.ptal.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static konkuk.ptal.dto.api.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements IReviewService {
    private final ReviewSessionRepository reviewSessionRepository;
    private final CodeFileRepository codeFileRepository;
    private final ReviewCommentRepository reviewCommentRepository;
    private final UserRepository userRepository;
    private final RevieweeRepository revieweeRepository;
    private final ReviewerRepository reviewerRepository;
    private final IFileService fileService;

    @Override
    @Transactional
    public ReviewSession createReviewSession(CreateReviewSessionRequest request) {
        Reviewee reviewee = revieweeRepository.findById(request.getRevieweeId())
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
        Reviewer reviewer = reviewerRepository.findById(request.getReviewerId())
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
        String absolutePath = fileService.saveCode(request.getGithubLink(), request.getBranchName());
        ReviewSession reviewSession = ReviewSession.createReviewSession(absolutePath, ReviewRequestStatus.PENDING, reviewer, reviewee, request);
        reviewSession = reviewSessionRepository.save(reviewSession);

        List<String> relativePaths = fileService.getCodeFileList(absolutePath);
        for (String relativePath : relativePaths) {
            CodeFile codeFile = CodeFile.createCodeFile(reviewSession, relativePath);
            codeFileRepository.save(codeFile);
        }
        return reviewSession;
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewSession getReviewSession(Long sessionId) {
        return reviewSessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
    }

    @Override
    @Transactional
    public ReviewComment createReviewComment(Long sessionId, CreateReviewCommentRequest request) {
        ReviewSession reviewSession = reviewSessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        CodeFile codeFile = null;
        ReviewCommentType commentType;

        if (request.getCodeFileId() != null && request.getLineNumber() != null) {
            codeFile = codeFileRepository.findById(request.getCodeFileId())
                    .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
            if (codeFile.getSessionId().getId() != sessionId) {
                throw new BadRequestException(FILE_NOT_BELONG_TO_SESSION);
            }
            commentType = ReviewCommentType.CODE_COMMENT;
        } else {
            commentType = ReviewCommentType.SESSION_COMMENT;
        }
        ReviewComment parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment = reviewCommentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
            if (parentComment.getReviewSession().getId() != sessionId) {
                throw new BadRequestException(PARENT_COMMENT_NOT_BELONG_TO_SESSION);
            }
        }
        ReviewComment comment = ReviewComment.createReviewComment(reviewSession,parentComment,codeFile,user,commentType,request);
        return reviewCommentRepository.save(comment);
    }


    @Override
    @Transactional(readOnly = true)
    public ReviewComment getReviewComments(Long sessionId, Long codeFileId) {
        reviewSessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        if (codeFileId != null) {
            return reviewCommentRepository.findByReviewSessionIdAndCodeFileIdAndCommentType(sessionId, codeFileId, ReviewCommentType.CODE_COMMENT);
        } else {
            return reviewCommentRepository.findByReviewSessionIdAndCommentTypeAndCodeFileIsNull(sessionId, ReviewCommentType.SESSION_COMMENT);
        }
    }

    @Override
    @Transactional
    public ReviewComment updateReviewComment(Long commentId, CreateReviewCommentRequest request) {
        ReviewComment comment = reviewCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        if (request.getContent() != null && !request.getContent().trim().isEmpty()) {
            comment.setContent(request.getContent());
        }
        return reviewCommentRepository.save(comment);
    }

    @Override
    @Transactional
    public boolean deleteReviewComment(Long commentId) {
        Optional<ReviewComment> optionalComment = reviewCommentRepository.findById(commentId);
        if (optionalComment.isPresent()) {
            ReviewComment comment = optionalComment.get();
            reviewCommentRepository.delete(comment);
            return true;
        }
        return false;
    }
}
