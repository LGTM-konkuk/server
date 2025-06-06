package konkuk.ptal.controller;

import konkuk.ptal.dto.api.ApiResponse;
import konkuk.ptal.dto.request.CreateReviewCommentRequest;
import konkuk.ptal.dto.request.UpdateReviewCommentRequest;
import konkuk.ptal.dto.response.ReadCommentResponse;
import konkuk.ptal.dto.response.ReadCommentsOfReviewResponse;
import konkuk.ptal.entity.ReviewComment;
import konkuk.ptal.service.IReviewService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class ReviewCommentController {

    private final IReviewService reviewService;

    @GetMapping("/review-submissions/{submissionId}/comments")
    public ResponseEntity<ApiResponse<ReadCommentsOfReviewResponse>> getReviewComments(@PathVariable Long submissionId){
        // TODO : 댓글에 대해 접근 권한이 있는지 보는 코드

        ReadCommentsOfReviewResponse response = reviewService.getReviewComments(submissionId, null);
        return ResponseEntity.ok(ApiResponse.success("댓글 조회 성공", response));
    }

    @PostMapping("/review-submissions/{submissionId}/comments")
    public ResponseEntity<ApiResponse<ReadCommentResponse>> createReviewComment(
            @PathVariable Long submissionId,
            @RequestBody CreateReviewCommentRequest request
    ){
        // TODO : 댓글에 대해 접근 권한이 있는지 보는 코드
        // TODO : 여기서 parentId를 어떻게 관리할지 보자
        // TODO : 여기서 fileId를 어떻게 관리할지 보자

        ReviewComment reviewComment = reviewService.createReviewComment(submissionId, request);
        return ResponseEntity.ok(ApiResponse.success("댓글 작성 성공", ReadCommentResponse.from(reviewComment)));
    }

    @GetMapping("/review-comments/{commentId}")
    public ResponseEntity<ApiResponse<ReadCommentResponse>> getReviewComment(@PathVariable String commentId){
        // TODO : 구현하기
        return null;
    }

    @PutMapping("/review-comments/{commentId}")
    public ResponseEntity<ApiResponse<ReadCommentResponse>> createReviewCommentWithParent(
            @PathVariable String commentId,
            @RequestBody CreateReviewCommentRequest request
    ) {
        // TODO : 댓글에 대해 접근 권한이 있는지 보는 코드
        // TODO : Service에서 타입 String으로 바꾸기

        ReviewComment reviewComment = reviewService.updateReviewComment(commentId, request);
        return ResponseEntity.ok(ApiResponse.success("댓글 수정 성공", ReadCommentResponse.from(reviewComment)));
    }

    // TODO: Put 관련 메서드 구현

    @DeleteMapping("/review-comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteReviewComment(@PathVariable String commentId){
        reviewService.deleteReviewComment(commentId);
        return ResponseEntity.ok(ApiResponse.success("댓글 삭제 성공", null));
    }

    @PostMapping("/review-comments/{commentId}/replies")
    public ResponseEntity<ApiResponse<ReadCommentResponse>> createReplyReviewComment(@PathVariable String commentId, @RequestBody UpdateReviewCommentRequest request){

        // TODO 구현
        return null;
    }

}
