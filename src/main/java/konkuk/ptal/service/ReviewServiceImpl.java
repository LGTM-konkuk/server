package konkuk.ptal.service;

import konkuk.ptal.domain.enums.ReviewCommentType;
import konkuk.ptal.domain.enums.ReviewRequestStatus;
import konkuk.ptal.dto.request.CreateReviewCommentRequest;
import konkuk.ptal.dto.request.CreateReviewSessionRequest;
import konkuk.ptal.dto.request.UpdateReviewCommentRequest;
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
        Reviewee reviewee = findRevieweeById(request.getRevieweeId());
        Reviewer reviewer = findReviewerById(request.getReviewerId());
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
        return findReviewSubmissionById(submissionId);
    }

    @Override
    @Transactional
    public ReviewComment createReviewComment(Long submissionId, CreateReviewCommentRequest request) {
        ReviewSubmission reviewSubmission = findReviewSubmissionById(submissionId);

        // TODO: 현재 인증된 사용자 정보를 가져와야 함 (현재는 임시로 reviewSubmission의 작성자 사용)
        // 실제 구현에서는 SecurityContext에서 현재 사용자를 가져와야 함
        User user = reviewSubmission.getReviewee().getUser(); // 임시로 리뷰이를 사용

        CodeFile codeFile = null;
        ReviewCommentType commentType;

        // filePath와 lineNumber 기반으로 댓글 유형 결정
        if (request.getFilePath() != null && !request.getFilePath().trim().isEmpty()) {
            // 파일 경로로 CodeFile 찾기
            codeFile = findCodeFileByPath(reviewSubmission, request.getFilePath());
            commentType = ReviewCommentType.CODE_COMMENT;
        } else {
            // 파일 경로가 없으면 세션 댓글
            commentType = ReviewCommentType.SESSION_COMMENT;
        }

        // 부모 댓글 처리
        ReviewComment parentComment = null;
        if (request.getParentCommentId() != null && !request.getParentCommentId().trim().isEmpty()) {
            parentComment = findReviewCommentById(request.getParentCommentId());
            
            // 부모 댓글이 같은 세션에 속하는지 확인
            if (!submissionId.equals(parentComment.getReviewSubmission().getId())) {
                throw new BadRequestException(PARENT_COMMENT_NOT_BELONG_TO_SESSION);
            }
            
            // 답글의 경우 부모 댓글의 파일과 라인 정보를 상속
            if (parentComment.getCodeFile() != null) {
                codeFile = parentComment.getCodeFile();
                commentType = ReviewCommentType.CODE_COMMENT;
            }
        }

        ReviewComment comment = ReviewComment.createReviewComment(
            reviewSubmission, 
            parentComment, 
            codeFile, 
            user, 
            commentType, 
            request
        );
        
        return reviewCommentRepository.save(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public ReadCommentsOfReviewResponse getReviewComments(Long submissionId, Long codeFileId) {
        findReviewSubmissionById(submissionId);

        List<ReviewComment> allComments = getAllCommentsBySubmissionAndCodeFile(submissionId, codeFileId);

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

    @Override
    @Transactional
    public ReviewComment updateReviewComment(String commentId, UpdateReviewCommentRequest request) {
        ReviewComment comment = findReviewCommentById(commentId);

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

    // Private helper methods for reducing code duplication

    /**
     * ReviewSubmission을 ID로 조회하는 헬퍼 메서드
     */
    private ReviewSubmission findReviewSubmissionById(Long submissionId) {
        return reviewSessionRepository.findById(submissionId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
    }

    /**
     * ReviewComment를 ID로 조회하는 헬퍼 메서드
     */
    private ReviewComment findReviewCommentById(String commentId) {
        return reviewCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
    }

    /**
     * Reviewee를 ID로 조회하는 헬퍼 메서드
     */
    private Reviewee findRevieweeById(Long revieweeId) {
        return revieweeRepository.findById(revieweeId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
    }

    /**
     * Reviewer를 ID로 조회하는 헬퍼 메서드
     */
    private Reviewer findReviewerById(Long reviewerId) {
        return reviewerRepository.findById(reviewerId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
    }

    /**
     * 파일 경로로 CodeFile을 조회하는 헬퍼 메서드
     */
    private CodeFile findCodeFileByPath(ReviewSubmission reviewSubmission, String filePath) {
        return codeFileRepository.findBySubmissionIdAndRelativePath(reviewSubmission, filePath)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
    }

    /**
     * 특정 세션과 코드 파일에 대한 모든 댓글을 조회하는 헬퍼 메서드
     */
    private List<ReviewComment> getAllCommentsBySubmissionAndCodeFile(Long submissionId, Long codeFileId) {
        if (codeFileId != null) {
            // 특정 코드 파일의 모든 댓글 조회 (부모 댓글과 답글 모두)
            return reviewCommentRepository.findByReviewSubmissionIdAndCodeFileId(submissionId, codeFileId);
        } else {
            // 세션 레벨 댓글과 그 답글들 조회
            List<ReviewComment> sessionComments = reviewCommentRepository.findByReviewSubmissionIdAndCommentTypeAndCodeFileIsNull(submissionId, ReviewCommentType.SESSION_COMMENT);
            
            // 세션 댓글들의 모든 답글들을 조회
            List<String> sessionCommentIds = sessionComments.stream()
                    .map(ReviewComment::getId)
                    .collect(Collectors.toList());
            
            return reviewCommentRepository.findByReviewSubmissionId(submissionId).stream()
                    .filter(comment -> 
                        // 세션 댓글이거나
                        (comment.getCommentType() == ReviewCommentType.SESSION_COMMENT && comment.getCodeFile() == null) ||
                        // 세션 댓글의 답글인 경우
                        isReplyToSessionComment(comment, sessionCommentIds))
                    .collect(Collectors.toList());
        }
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
}
