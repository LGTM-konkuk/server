package konkuk.ptal.service;

import konkuk.ptal.domain.UserPrincipal;
import konkuk.ptal.domain.enums.ReviewCommentType;
import konkuk.ptal.domain.enums.ReviewSubmissionStatus;
import konkuk.ptal.domain.enums.ReviewSubmissionType;
import konkuk.ptal.dto.request.*;
import konkuk.ptal.dto.response.*;
import konkuk.ptal.entity.*;
import konkuk.ptal.exception.BadRequestException;
import konkuk.ptal.exception.EntityNotFoundException;
import konkuk.ptal.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static konkuk.ptal.dto.api.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements IReviewService {
    private final ReviewSubmissionRepository reviewSubmissionRepository;
    private final CodeFileRepository codeFileRepository;
    private final ReviewCommentRepository reviewCommentRepository;
    private final UserRepository userRepository;
    private final RevieweeRepository revieweeRepository;
    private final ReviewerRepository reviewerRepository;
    private final ReviewRepository reviewRepository;
    private final IFileService fileService;

    @Override
    @Transactional
    public ReviewSubmission createReviewSubmission(CreateReviewSubmissionRequest request, UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
        Reviewee reviewee = revieweeRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        Reviewer reviewer = null;
        if (request.getReviewerId() != null) {
            reviewer = reviewerRepository.findById(request.getReviewerId())
                    .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
        }
        ReviewSubmission reviewSubmission = ReviewSubmission.createReviewSubmission(ReviewSubmissionStatus.PENDING, reviewer, reviewee, request);
        reviewSubmission = reviewSubmissionRepository.save(reviewSubmission);
        fileService.createCodeFilesForSubmission(reviewSubmission);

        return reviewSubmission;

    }

    @Override
    @Transactional(readOnly = true)
    public ReviewSubmission getReviewSubmission(Long submissionId, UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUserId();
        Optional<Reviewer> reviewer = reviewerRepository.findByUser_Id(userId);

        ReviewSubmission reviewSubmission;

        if (reviewer.isPresent()) {
            reviewSubmission = reviewSubmissionRepository
                    .findByIdAndReviewer_User_IdOrIdAndReviewerIsNullAndStatus(
                            submissionId, userId, submissionId, ReviewSubmissionStatus.PENDING
                    )
                    .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
        } else {
            reviewSubmission = reviewSubmissionRepository
                    .findByIdAndReviewee_User_Id(submissionId, userId)
                    .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
        }
        return reviewSubmission;
    }

    @Override
    public ListReviewSubmissionResponse getReviewSubmissions(ReviewSubmissionType type, int page, int size, UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUserId();
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewSubmission> reviewSubmissionPage;
        switch (type) {
            case SENT:
                Reviewee revieweeForSent = revieweeRepository.findByUser_Id(userId)
                        .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
                reviewSubmissionPage = reviewSubmissionRepository.findByReviewee(revieweeForSent, pageable);
                break;
            case RECEIVED:
                Reviewer reviewerForReceived = reviewerRepository.findByUser_Id(userId)
                        .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
                reviewSubmissionPage = reviewSubmissionRepository.findByReviewer(reviewerForReceived, pageable);
                break;
            case ALL:
            default:
                Optional<Reviewer> currentReviewer = reviewerRepository.findByUser_Id(userId);
                if (currentReviewer.isPresent()) {
                    reviewSubmissionPage = reviewSubmissionRepository.findAllSubmissionsForReviewer(userId, ReviewSubmissionStatus.PENDING, pageable);
                }else{
                    Reviewee reviewee = revieweeRepository.findByUser_Id(userId)
                            .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
                    reviewSubmissionPage = reviewSubmissionRepository.findByReviewee(reviewee, pageable);
                }
                break;
        }
        List<ReadReviewSubmissionResponse> content = reviewSubmissionPage.getContent().stream()
                .map(submission -> {
                    ProjectFileSystemResponse fileSystem = fileService.getProjectFileSystem(
                            submission.getGitUrl(),
                            submission.getBranch(),
                            submission.getId()
                    );
                    return ReadReviewSubmissionResponse.from(submission, fileSystem);
                })
                .collect(Collectors.toList());

        return new ListReviewSubmissionResponse(reviewSubmissionPage, content);
    }

    @Override
    public ReviewSubmission cancelReviewSubmission(Long submissionId, UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUserId();

        ReviewSubmission reviewSubmission = reviewSubmissionRepository
                .findByIdAndReviewee_User_Id(submissionId, userId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        if (reviewSubmission.getStatus() != ReviewSubmissionStatus.PENDING &&
                reviewSubmission.getStatus() != ReviewSubmissionStatus.APPROVED) {
            throw new BadRequestException(SUBMISSION_CANCEL_UNAVAILABLE);
        }

        reviewSubmission.setStatus(ReviewSubmissionStatus.CANCELED);
        ReviewSubmission updatedSubmission = reviewSubmissionRepository.save(reviewSubmission);

        return updatedSubmission;
    }

    @Override
    @Transactional
    public ReviewComment createReviewComment(Long submissionId, CreateReviewCommentRequest request, UserPrincipal userPrincipal) {
        ReviewSubmission reviewSubmission = findReviewSubmissionById(submissionId);

        // 인증된 사용자 정보를 가져옴
        User user = findUserById(userPrincipal.getUserId());

        if (reviewSubmission.getStatus() == ReviewSubmissionStatus.PENDING) {
            reviewSubmission.setStatus(ReviewSubmissionStatus.APPROVED);
        }

        if (reviewSubmission.getReviewer() == null) {
            Optional<Reviewer> optionalReviewer = reviewerRepository.findByUser_Id(user.getId());
            if (optionalReviewer.isPresent()) {
                Reviewer actualReviewer = optionalReviewer.get();
                reviewSubmission.setReviewer(actualReviewer);
                reviewSubmissionRepository.save(reviewSubmission);
            }
        }
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
    public ReadCommentsOfReviewResponse getReviewComments(Long submissionId, String codeFile) {
        findReviewSubmissionById(submissionId);

        List<ReviewComment> allComments = getAllCommentsBySubmissionAndCodeFile(submissionId, codeFile);

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

    @Override
    @Transactional(readOnly = true)
    public ReviewComment getReviewComment(String commentId) {
        return findReviewCommentById(commentId);
    }

    @Override
    @Transactional
    public Review createReview(Long submissionId, CreateReviewRequest request, UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUserId();

        ReviewSubmission reviewSubmission = reviewSubmissionRepository
                .findByIdAndReviewer_User_Id(submissionId, userId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        // PENDING 또는 APPROVED 상태일 때만 리뷰 생성을 허용합니다.
        if (reviewSubmission.getStatus() != ReviewSubmissionStatus.PENDING &&
                reviewSubmission.getStatus() != ReviewSubmissionStatus.APPROVED) {
            throw new BadRequestException(REVIEW_UNAVAILABLE);
        }

        if (reviewRepository.findByReviewSubmissionId(reviewSubmission.getId()).isPresent()) {
            throw new BadRequestException(REVIEW_ALREADY_EXIST);
        }

        Review newReview = Review.createReview(reviewSubmission, request);
        Review savedReview = reviewRepository.save(newReview);

        reviewSubmission.setStatus(ReviewSubmissionStatus.REVIEWED);
        reviewSubmissionRepository.save(reviewSubmission);

        return savedReview;
    }

    @Override
    @Transactional(readOnly = true)
    public Review getReview(Long reviewId, UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUserId();

        Review review = reviewRepository
                .findByIdAndReviewSubmission_Reviewee_User_IdOrIdAndReviewSubmission_Reviewer_User_Id(
                        reviewId, userId,
                        reviewId, userId
                )
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        return review;
    }

    @Override
    @Transactional
    public Review updateReview(Long reviewId, UpdateReviewRequest request, UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUserId();

        Review review = reviewRepository
                .findByIdAndReviewSubmission_Reviewer_User_Id(reviewId, userId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        review.updateConclusion(request.getReviewContent());

        return reviewRepository.save(review);
    }

    @Override
    @Transactional(readOnly = true)
    public ListReviewsResponse getReviews(Long submissionId, Long reviewerId, Long revieweeId, int page, int size, UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUserId();
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviewPage;

        // 조회 조건에 따라 동적 쿼리 (또는 Querydsl 사용 고려)
        if (submissionId != null) {
            reviewPage = reviewRepository.findByReviewSubmissionIdAndReviewSubmission_Reviewee_User_IdOrReviewSubmission_Reviewer_User_Id(
                    submissionId, userId, submissionId, userId, pageable
            );
        } else if (reviewerId != null && reviewerId.equals(userId)) {
            reviewPage = reviewRepository.findByReviewSubmission_Reviewer_User_Id(reviewerId, pageable);
        } else if (revieweeId != null && revieweeId.equals(userId)) {
            reviewPage = reviewRepository.findByReviewSubmission_Reviewee_User_Id(revieweeId, pageable);
        } else {
            reviewPage = reviewRepository.findByReviewSubmission_Reviewee_User_IdOrReviewSubmission_Reviewer_User_Id(
                    userId, userId, pageable
            );
        }
        List<ReadReviewResponse> content = reviewPage.getContent().stream()
                .map(review -> {
                    ProjectFileSystemResponse fileSystem = fileService.getProjectFileSystem(
                            review.getReviewSubmission().getGitUrl(),
                            review.getReviewSubmission().getBranch(),
                            review.getReviewSubmission().getId()
                    );
                    return ReadReviewResponse.from(review, fileSystem);
                })
                .collect(Collectors.toList());

        return new ListReviewsResponse(reviewPage, content);
    }


    /**
     * ReviewSubmission을 ID로 조회하는 헬퍼 메서드
     */
    private ReviewSubmission findReviewSubmissionById(Long submissionId) {
        return reviewSubmissionRepository.findById(submissionId)
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
     * User를 ID로 조회하는 헬퍼 메서드
     */
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
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
    private List<ReviewComment> getAllCommentsBySubmissionAndCodeFile(Long submissionId, String filePath) {
        if (filePath != null && !filePath.trim().isEmpty()) {
            // 파일 경로로 CodeFile 찾기
            ReviewSubmission reviewSubmission = findReviewSubmissionById(submissionId);
            CodeFile codeFile = findCodeFileByPath(reviewSubmission, filePath);
            
            // 특정 코드 파일의 모든 댓글 조회 (부모 댓글과 답글 모두)
            return reviewCommentRepository.findByReviewSubmissionIdAndCodeFileId(submissionId, codeFile.getId());
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
