package konkuk.ptal.service;

import konkuk.ptal.domain.UserPrincipal;
import konkuk.ptal.domain.enums.ReviewSubmissionStatus;
import konkuk.ptal.dto.api.ErrorCode;
import konkuk.ptal.entity.ReviewComment;
import konkuk.ptal.entity.ReviewSubmission;
import konkuk.ptal.entity.Reviewer;
import konkuk.ptal.exception.BadRequestException;
import konkuk.ptal.repository.ReviewRepository;
import konkuk.ptal.repository.ReviewerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements IAuthorizationService {

    private final IReviewService reviewService;
    private final ReviewerRepository reviewerRepository;

    @Override
    public void validateReviewSubmissionAccess(Long submissionId, UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUserId();
        ReviewSubmission reviewSubmission = reviewService.getReviewSubmission(submissionId, userPrincipal);
        Optional<Reviewer> currentReviewer = reviewerRepository.findByUser_Id(userId);

        boolean hasAccess = false;

        if (reviewSubmission.getReviewee().getUser().getId().equals(userId)) {
            hasAccess = true;
        }

        if (!hasAccess && currentReviewer.isPresent()) {
            if (reviewSubmission.getReviewer() != null &&
                    reviewSubmission.getReviewer().getUser().getId().equals(userId)) {
                hasAccess = true;
            }
            else if (reviewSubmission.getReviewer() == null &&
                    reviewSubmission.getStatus() == ReviewSubmissionStatus.PENDING) {
                hasAccess = true;
            }
        }
        if (!hasAccess) {
            throw new BadRequestException(ErrorCode.ACCESS_DENIED);
        }
    }

    @Override
    public void validateReviewCommentAccess(String commentId, UserPrincipal userPrincipal) {
        ReviewComment reviewComment = reviewService.getReviewComment(commentId);
        Long submissionId = reviewComment.getReviewSubmission().getId();
        validateReviewSubmissionAccess(submissionId, userPrincipal);
    }

    @Override
    public void validateReviewCommentModifyAccess(String commentId, UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUserId();
        ReviewComment reviewComment = reviewService.getReviewComment(commentId);

        // 1. 먼저 해당 리뷰에 접근 권한이 있는지 확인
        Long submissionId = reviewComment.getReviewSubmission().getId();
        validateReviewSubmissionAccess(submissionId, userPrincipal);

        // 2. 댓글 작성자 본인인지 확인 (추가적인 권한 검사)
        boolean isCommentAuthor = reviewComment.getUser().getId().equals(userId);
        if (!isCommentAuthor) {
            throw new BadRequestException(ErrorCode.ACCESS_DENIED);
        }
    }
} 