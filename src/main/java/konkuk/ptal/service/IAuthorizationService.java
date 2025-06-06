package konkuk.ptal.service;

public interface IAuthorizationService {
    
    /**
     * 사용자가 해당 ReviewSubmission에 대해 접근 권한이 있는지 확인합니다.
     * 해당 ReviewSubmission의 reviewee 또는 reviewer인 경우에만 접근을 허용합니다.
     * 
     * @param submissionId 검증할 ReviewSubmission의 ID
     * @param userId 현재 인증된 사용자의 ID
     * @throws BadRequestException 접근 권한이 없는 경우
     */
    void validateReviewSubmissionAccess(Long submissionId, Long userId);
    
    /**
     * 댓글 ID를 통해 사용자의 접근 권한을 확인합니다.
     * 
     * @param commentId 확인할 댓글의 ID
     * @param userId 현재 인증된 사용자의 ID
     * @throws BadRequestException 접근 권한이 없는 경우
     */
    void validateReviewCommentAccess(String commentId, Long userId);
    
    /**
     * 사용자가 특정 댓글을 수정/삭제할 권한이 있는지 확인합니다.
     * (댓글 작성자 본인이거나 관리자인 경우)
     * 
     * @param commentId 확인할 댓글의 ID
     * @param userId 현재 인증된 사용자의 ID
     * @throws BadRequestException 접근 권한이 없는 경우
     */
    void validateReviewCommentModifyAccess(String commentId, Long userId);
} 