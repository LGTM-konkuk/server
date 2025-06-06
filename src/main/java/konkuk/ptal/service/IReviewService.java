package konkuk.ptal.service;

import konkuk.ptal.domain.UserPrincipal;
import konkuk.ptal.domain.enums.ReviewSubmissionType;
import konkuk.ptal.dto.request.*;
import konkuk.ptal.dto.response.ListReviewSubmissionResponse;
import konkuk.ptal.dto.response.ListReviewsResponse;
import konkuk.ptal.dto.response.ReadCommentsOfReviewResponse;
import konkuk.ptal.entity.Review;
import konkuk.ptal.entity.ReviewComment;
import konkuk.ptal.entity.ReviewSubmission;
import konkuk.ptal.exception.BadRequestException;
import konkuk.ptal.exception.EntityNotFoundException;

public interface IReviewService {

    /**
     * 새로운 코드 리뷰 세션을 생성합니다.
     *
     * @param request 리뷰 세션 생성을 위한 상세 정보를 담고 있는 요청 객체
     * @return 새로 생성된 {@link ReviewSubmission} 객체입니다.
     */
    ReviewSubmission createReviewSubmission(CreateReviewSubmissionRequest request, UserPrincipal userPrincipal);

    /**
     * 특정 코드 리뷰 세션의 상세 정보를 조회합니다.
     *
     * @param submissionId 리뷰 세션의 고유 식별자입니다.
     * @return 기본 세션 정보와 해당 세션 내의 모든 파일 목록을 포함하는 {@link ReviewSubmission} 객체입니다.
     */
    ReviewSubmission getReviewSubmission(Long submissionId, UserPrincipal userPrincipal);

    /**
     * 리뷰 제출(요청) 목록을 조회합니다.
     *
     * @param type          조회할 제출 목록의 타입 (sent, received, all)
     * @param page          페이지 번호 (0부터 시작)
     * @param size          한 페이지당 항목 수
     * @param userPrincipal 현재 인증된 사용자 정보
     * @return 필터링 및 페이지네이션된 리뷰 제출 목록 DTO
     */
    ListReviewSubmissionResponse getReviewSubmissions(ReviewSubmissionType type, int page, int size, UserPrincipal userPrincipal);

    /**
     * 특정 리뷰 제출(요청)을 취소합니다.
     *
     * @param submissionId  취소할 리뷰 제출의 ID
     * @param userPrincipal 현재 인증된 사용자 정보
     * @return 취소된 리뷰 제출의 상세 정보 DTO
     * @throws EntityNotFoundException 해당 submissionId를 찾을 수 없거나 접근 권한이 없을 때
     * @throws BadRequestException     이미 취소되었거나 리뷰가 시작된 제출을 취소하려 할 때
     */
    ReviewSubmission cancelReviewSubmission(Long submissionId, UserPrincipal userPrincipal);


    /**
     * 특정 리뷰 세션 내에 새로운 댓글을 생성합니다.
     * 댓글의 유형(코드 댓글 또는 세션 댓글)은 요청에 `snapshotFileId`와 `lineNumber`가 있는지에 따라 결정됩니다.
     *
     * @param submissionId 댓글이 속할 리뷰 세션의 고유 식별자입니다.
     * @param request      댓글 내용, 선택적으로 파일 정보(미기재 시 리뷰 세션 댓글 타입)을 포함합니다.
     * @param userId       댓글을 작성하는 사용자의 ID입니다.
     */
    ReviewComment createReviewComment(Long submissionId, CreateReviewCommentRequest request, UserPrincipal userPrincipal);

    /**
     * 특정 리뷰 세션의 댓글을 조회하며, 선택적으로 코드 파일별로 필터링할 수 있습니다.
     * `codeFileId`가 제공되면 해당 파일에 대한 코드 댓글만 반환됩니다.
     * `codeFileId`가 null이면 해당 리뷰 세션의 모든 세션 레벨 댓글이 반환됩니다.
     * 댓글은 스레드를 나타내는 계층적 구조로 반환됩니다.
     *
     * @param submissionId 리뷰 세션의 고유 식별자입니다.
     * @param codeFileId   댓글을 조회할 코드 파일의 고유 식별자입니다.
     *                     세션 레벨 댓글을 조회하려면 null일 수 있습니다.
     */
    ReadCommentsOfReviewResponse getReviewComments(Long submissionId, Long codeFileId);

    /**
     * 기존 댓글을 업데이트합니다.
     *
     * @param commentId 업데이트할 댓글의 고유 식별자입니다.
     * @param request   `content` 또는 `status`와 같은 업데이트된 댓글 상세 정보를 포함하는 요청 객체입니다.
     */
    ReviewComment updateReviewComment(String commentId, UpdateReviewCommentRequest request);

    /**
     * 댓글의 상태를 DELETED로 변경하여 (소프트 삭제) 댓글을 삭제합니다.
     *
     * @param commentId 삭제할 댓글의 고유 식별자입니다.
     * @return 댓글이 성공적으로 삭제(소프트 삭제)되었으면 `true`, 그렇지 않으면 `false`를 반환합니다.
     */
    boolean deleteReviewComment(String commentId);

    /**
     * 댓글 ID로 해당 댓글 정보를 조회합니다.
     *
     * @param commentId 조회할 댓글의 고유 식별자입니다.
     * @return 댓글 엔티티를 반환합니다.
     */
    ReviewComment getReviewComment(String commentId);

    // 새로운 리뷰 작성
    Review createReview(Long submissionId, CreateReviewRequest request, UserPrincipal userPrincipal);

    // 리뷰 상세 조회
    Review getReview(Long reviewId, UserPrincipal userPrincipal);

    // 리뷰 수정
    Review updateReview(Long reviewId, UpdateReviewRequest request, UserPrincipal userPrincipal);

    // 리뷰 목록 조회
    ListReviewsResponse getReviews(Long submissionId, Long reviewerId, Long revieweeId, int page, int size, UserPrincipal userPrincipal);
}
