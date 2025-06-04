package konkuk.ptal.service;

import konkuk.ptal.dto.request.CreateReviewCommentRequest;
import konkuk.ptal.dto.request.CreateReviewSessionRequest;
import konkuk.ptal.entity.ReviewComment;
import konkuk.ptal.entity.ReviewSession;

public interface IReviewService {

    /**
     * 새로운 코드 리뷰 세션을 생성합니다.
     *
     * @param request 리뷰 세션 생성을 위한 상세 정보를 담고 있는 요청 객체
     * @return 새로 생성된 {@link ReviewSession} 객체입니다.
     */
    ReviewSession createReviewSession(CreateReviewSessionRequest request);

    /**
     * 특정 코드 리뷰 세션의 상세 정보를 조회합니다.
     *
     * @param sessionId 리뷰 세션의 고유 식별자입니다.
     * @return 기본 세션 정보와 해당 세션 내의 모든 파일 목록을 포함하는 {@link ReviewSession} 객체입니다.
     */
    ReviewSession getReviewSession(Long sessionId);

    /**
     * 특정 리뷰 세션 내에 새로운 댓글을 생성합니다.
     * 댓글의 유형(코드 댓글 또는 세션 댓글)은 요청에 `snapshotFileId`와 `lineNumber`가 있는지에 따라 결정됩니다.
     *
     * @param sessionId 댓글이 속할 리뷰 세션의 고유 식별자입니다.
     * @param request   댓글 내용, 선택적으로 파일 정보(미기재 시 리뷰 세션 댓글 타입)을 포함합니다.
     */
    ReviewComment createReviewComment(Long sessionId, CreateReviewCommentRequest request);

    /**
     * 특정 리뷰 세션의 댓글을 조회하며, 선택적으로 코드 파일별로 필터링할 수 있습니다.
     * `codeFileId`가 제공되면 해당 파일에 대한 코드 댓글만 반환됩니다.
     * `codeFileId`가 null이면 해당 리뷰 세션의 모든 세션 레벨 댓글이 반환됩니다.
     * 댓글은 스레드를 나타내는 계층적 구조로 반환됩니다.
     *
     * @param sessionId  리뷰 세션의 고유 식별자입니다.
     * @param codeFileId 댓글을 조회할 코드 파일의 고유 식별자입니다.
     *                   세션 레벨 댓글을 조회하려면 null일 수 있습니다.
     */
    ReviewComment getReviewComments(Long sessionId, Long codeFileId);

    /**
     * 기존 댓글을 업데이트합니다.
     * @param commentId 업데이트할 댓글의 고유 식별자입니다.
     * @param request   `content` 또는 `status`와 같은 업데이트된 댓글 상세 정보를 포함하는 요청 객체입니다.
     */
    ReviewComment updateReviewComment(Long commentId, CreateReviewCommentRequest request);

    /**
     * 댓글의 상태를 DELETED로 변경하여 (소프트 삭제) 댓글을 삭제합니다.
     * @param commentId 삭제할 댓글의 고유 식별자입니다.
     * @return 댓글이 성공적으로 삭제(소프트 삭제)되었으면 `true`, 그렇지 않으면 `false`를 반환합니다.
     */
    boolean deleteReviewComment(Long commentId);
}
