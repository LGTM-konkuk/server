package konkuk.ptal.service;

import konkuk.ptal.domain.enums.ReviewCommentType;
import konkuk.ptal.domain.enums.ReviewRequestStatus;
import konkuk.ptal.dto.request.CreateReviewCommentRequest;
import konkuk.ptal.dto.request.CreateReviewSessionRequest;
import konkuk.ptal.dto.response.ReadCommentResponse;
import konkuk.ptal.dto.response.ReadCommentsOfReviewResponse;
import konkuk.ptal.entity.*;
import konkuk.ptal.exception.BadRequestException;
import konkuk.ptal.exception.EntityNotFoundException;
import konkuk.ptal.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public ReadCommentsOfReviewResponse getReviewComments(Long submissionId, Long codeFileId) {
        reviewSessionRepository.findById(submissionId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        List<ReviewComment> allComments;
        
        if (codeFileId != null) {
            // 특정 코드 파일의 모든 댓글 조회 (부모 댓글과 답글 모두)
            allComments = reviewCommentRepository.findByReviewSubmissionIdAndCodeFileId(submissionId, codeFileId);
        } else {
            // 세션 레벨 댓글과 그 답글들 조회
            List<ReviewComment> sessionComments = reviewCommentRepository.findByReviewSubmissionIdAndCommentTypeAndCodeFileIsNull(submissionId, ReviewCommentType.SESSION_COMMENT);
            
            // 세션 댓글들의 모든 답글들을 조회
            List<String> sessionCommentIds = sessionComments.stream()
                    .map(ReviewComment::getId)
                    .collect(Collectors.toList());
            
            allComments = reviewCommentRepository.findByReviewSubmissionId(submissionId).stream()
                    .filter(comment -> 
                        // 세션 댓글이거나
                        (comment.getCommentType() == ReviewCommentType.SESSION_COMMENT && comment.getCodeFile() == null) ||
                        // 세션 댓글의 답글인 경우
                        isReplyToSessionComment(comment, sessionCommentIds))
                    .collect(Collectors.toList());
        }

        // 부모 댓글들만 필터링 (parentComment가 null인 것들)
        List<ReviewComment> parentComments = allComments.stream()
                .filter(comment -> comment.getParentComment() == null)
                .collect(Collectors.toList());

        // 각 부모 댓글에 대해 답글들을 찾아서 계층적 구조 생성
        List<ReadCommentResponse> hierarchicalComments = parentComments.stream()
                .map(parentComment -> buildCommentHierarchy(parentComment, allComments))
                .collect(Collectors.toList());

        return ReadCommentsOfReviewResponse.builder()
                .totalComments(parentComments.size()) // 부모 댓글 수만 카운트
                .content(hierarchicalComments)
                .build();
    }

    /**
     * 댓글이 세션 댓글의 답글인지 확인하는 헬퍼 메서드
     */
    private boolean isReplyToSessionComment(ReviewComment comment, List<String> sessionCommentIds) {
        ReviewComment current = comment;
        while (current.getParentComment() != null) {
            if (sessionCommentIds.contains(current.getParentComment().getId())) {
                return true;
            }
            current = current.getParentComment();
        }
        return false;
    }

    /**
     * 댓글의 계층적 구조를 생성하는 헬퍼 메서드
     */
    private ReadCommentResponse buildCommentHierarchy(ReviewComment comment, List<ReviewComment> allComments) {
        // 현재 댓글의 직접적인 답글들을 찾기
        List<ReviewComment> directReplies = allComments.stream()
                .filter(reply -> reply.getParentComment() != null && 
                        reply.getParentComment().getId().equals(comment.getId()))
                .collect(Collectors.toList());

        // 각 답글에 대해서도 재귀적으로 계층 구조 생성
        List<ReadCommentResponse> replyResponses = directReplies.stream()
                .map(reply -> buildCommentHierarchy(reply, allComments))
                .collect(Collectors.toList());

        // ReadCommentResponse 생성
        ReadCommentResponse response = ReadCommentResponse.from(comment);
        response.setReplies(replyResponses);
        
        return response;
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
