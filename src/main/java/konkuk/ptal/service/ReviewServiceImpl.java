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
    public ReviewSubmission createReviewSession(CreateReviewSessionRequest request) {
        Reviewee reviewee = revieweeRepository.findById(request.getRevieweeId())
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
        Reviewer reviewer = reviewerRepository.findById(request.getReviewerId())
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
        String absolutePath = fileService.saveCode(request.getGithubLink(), request.getBranchName());
        ReviewSubmission reviewSubmission = ReviewSubmission.createReviewSession(absolutePath, ReviewRequestStatus.PENDING, reviewer, reviewee, request);
        reviewSubmission = reviewSessionRepository.save(reviewSubmission);

        List<String> relativePaths = fileService.getCodeFileList(absolutePath);
        for (String relativePath : relativePaths) {
            CodeFile codeFile = CodeFile.createCodeFile(reviewSubmission, relativePath);
            codeFileRepository.save(codeFile);
        }
        return reviewSubmission;
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewSubmission getReviewSession(Long submissionId) {
        return reviewSessionRepository.findById(submissionId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
    }

    @Override
    @Transactional
    public ReviewComment createReviewComment(Long submissionId, CreateReviewCommentRequest request) {
        ReviewSubmission reviewSubmission = reviewSessionRepository.findById(submissionId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        CodeFile codeFile = null;
        ReviewCommentType commentType;

        if (request.getCodeFileId() != null && request.getLineNumber() != null) {
            codeFile = codeFileRepository.findById(request.getCodeFileId())
                    .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
            if (codeFile.getSessionId().getId() != submissionId) {
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
            if (parentComment.getReviewSubmission().getId() != submissionId) {
                throw new BadRequestException(PARENT_COMMENT_NOT_BELONG_TO_SESSION);
            }
        }
        ReviewComment comment = ReviewComment.createReviewComment(reviewSubmission,parentComment,codeFile,user,commentType,request);
        return reviewCommentRepository.save(comment);
    }


    @Override
    @Transactional(readOnly = true)
    public List<ReviewComment> getReviewComments(Long submissionId, Long codeFileId) {
        reviewSessionRepository.findById(submissionId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        if (codeFileId != null) {
            return reviewCommentRepository.findByReviewSessionIdAndCodeFileIdAndCommentType(submissionId, codeFileId, ReviewCommentType.CODE_COMMENT);
        } else {
            return reviewCommentRepository.findByReviewSessionIdAndCommentTypeAndCodeFileIsNull(submissionId, ReviewCommentType.SESSION_COMMENT);
        }
    }

    @Override
    @Transactional
    public ReviewComment updateReviewComment(String commentId, CreateReviewCommentRequest request) {
        ReviewComment comment = reviewCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        if (request.getContent() != null && !request.getContent().trim().isEmpty()) {
            comment.setContent(request.getContent());
        }
        return reviewCommentRepository.save(comment);
    }

    @Override
    @Transactional
    public boolean deleteReviewComment(String commentId) {
        Optional<ReviewComment> optionalComment = reviewCommentRepository.findById(commentId);
        if (optionalComment.isPresent()) {
            ReviewComment comment = optionalComment.get();
            reviewCommentRepository.delete(comment);
            return true;
        }
        return false;
    }
}
