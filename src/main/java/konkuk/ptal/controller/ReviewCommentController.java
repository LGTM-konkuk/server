package konkuk.ptal.controller;

import konkuk.ptal.domain.UserPrincipal;
import konkuk.ptal.dto.api.ApiResponse;
import konkuk.ptal.dto.request.CreateReviewCommentRequest;
import konkuk.ptal.dto.request.UpdateReviewCommentRequest;
import konkuk.ptal.dto.response.ReadCommentResponse;
import konkuk.ptal.dto.response.ReadCommentsOfReviewResponse;
import konkuk.ptal.entity.ReviewComment;
import konkuk.ptal.service.IReviewService;
import konkuk.ptal.service.IAuthorizationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class ReviewCommentController {

    private final IReviewService reviewService;
    private final IAuthorizationService authorizationService;

    @GetMapping("/review-submissions/{submissionId}/comments")
    public ResponseEntity<ApiResponse<ReadCommentsOfReviewResponse>> getReviewComments(
            @PathVariable Long submissionId,
            @AuthenticationPrincipal UserPrincipal userPrincipal){
        authorizationService.validateReviewSubmissionAccess(submissionId, userPrincipal.getUserId());

        ReadCommentsOfReviewResponse response = reviewService.getReviewComments(submissionId, null);
        return ResponseEntity.ok(ApiResponse.success("댓글 조회 성공", response));
    }

    @PostMapping("/review-submissions/{submissionId}/comments")
    public ResponseEntity<ApiResponse<ReadCommentResponse>> createReviewComment(
            @PathVariable Long submissionId,
            @RequestBody CreateReviewCommentRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ){
        authorizationService.validateReviewSubmissionAccess(submissionId, userPrincipal.getUserId());

        ReviewComment reviewComment = reviewService.createReviewComment(submissionId, request, userPrincipal.getUserId());
        return ResponseEntity.ok(ApiResponse.success("댓글 작성 성공", ReadCommentResponse.from(reviewComment)));
    }

    @GetMapping("/review-comments/{commentId}")
    public ResponseEntity<ApiResponse<ReadCommentResponse>> getReviewComment(
            @PathVariable String commentId,
            @AuthenticationPrincipal UserPrincipal userPrincipal){
        authorizationService.validateReviewCommentAccess(commentId, userPrincipal.getUserId());
        
        ReviewComment reviewComment = reviewService.getReviewComment(commentId);
        return ResponseEntity.ok(ApiResponse.success("댓글 조회 성공", ReadCommentResponse.from(reviewComment)));
    }

    @PutMapping("/review-comments/{commentId}")
    public ResponseEntity<ApiResponse<ReadCommentResponse>> updateReviewComment(
            @PathVariable String commentId,
            @RequestBody UpdateReviewCommentRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        authorizationService.validateReviewCommentModifyAccess(commentId, userPrincipal.getUserId());
        
        ReviewComment reviewComment = reviewService.updateReviewComment(commentId, request);
        return ResponseEntity.ok(ApiResponse.success("댓글 수정 성공", ReadCommentResponse.from(reviewComment)));
    }

    @DeleteMapping("/review-comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteReviewComment(
            @PathVariable String commentId,
            @AuthenticationPrincipal UserPrincipal userPrincipal){
        authorizationService.validateReviewCommentModifyAccess(commentId, userPrincipal.getUserId());
        
        reviewService.deleteReviewComment(commentId);
        return ResponseEntity.ok(ApiResponse.success("댓글 삭제 성공", null));
    }

    @PostMapping("/review-comments/{commentId}/replies")
    public ResponseEntity<ApiResponse<ReadCommentResponse>> createReplyReviewComment(
            @PathVariable String commentId, 
            @RequestBody CreateReviewCommentRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal){
        authorizationService.validateReviewCommentAccess(commentId, userPrincipal.getUserId());
        
        ReviewComment parentComment = reviewService.getReviewComment(commentId);
        Long submissionId = parentComment.getReviewSubmission().getId();
        
        CreateReviewCommentRequest replyRequest = new CreateReviewCommentRequest();
        replyRequest.setContent(request.getContent());
        replyRequest.setParentCommentId(commentId);
        
        ReviewComment replyComment = reviewService.createReviewComment(submissionId, replyRequest, userPrincipal.getUserId());
        return ResponseEntity.ok(ApiResponse.success("답글 작성 성공", ReadCommentResponse.from(replyComment)));
    }
}
