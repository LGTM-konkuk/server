package konkuk.ptal.service;

import konkuk.ptal.dto.api.ErrorCode;
import konkuk.ptal.entity.ReviewComment;
import konkuk.ptal.entity.ReviewSubmission;
import konkuk.ptal.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements IAuthorizationService {
    
    private final IReviewService reviewService;
    
    @Override
    public void validateReviewSubmissionAccess(Long submissionId, Long userId) {
        ReviewSubmission reviewSubmission = reviewService.getReviewSession(submissionId);
        
        // 해당 ReviewSubmission의 reviewee나 reviewer인지 확인
        boolean isReviewee = reviewSubmission.getReviewee().getUser().getId().equals(userId);
        boolean isReviewer = reviewSubmission.getReviewer().getUser().getId().equals(userId);
        
        if (!isReviewee && !isReviewer) {
            throw new BadRequestException(ErrorCode.ACCESS_DENIED);
        }
    }
    
    @Override
    public void validateReviewCommentAccess(String commentId, Long userId) {
        ReviewComment reviewComment = reviewService.getReviewComment(commentId);
        Long submissionId = reviewComment.getReviewSubmission().getId();
        validateReviewSubmissionAccess(submissionId, userId);
    }
    
    @Override
    public void validateReviewCommentModifyAccess(String commentId, Long userId) {
        ReviewComment reviewComment = reviewService.getReviewComment(commentId);
        
        // 1. 먼저 해당 리뷰에 접근 권한이 있는지 확인
        Long submissionId = reviewComment.getReviewSubmission().getId();
        validateReviewSubmissionAccess(submissionId, userId);
        
        // 2. 댓글 작성자 본인인지 확인 (추가적인 권한 검사)
        boolean isCommentAuthor = reviewComment.getUser().getId().equals(userId);
        if (!isCommentAuthor) {
            throw new BadRequestException(ErrorCode.ACCESS_DENIED);
        }
    }
} 