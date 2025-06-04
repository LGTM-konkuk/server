package konkuk.ptal.controller;

import konkuk.ptal.domain.UserPrincipal;
import konkuk.ptal.dto.api.ApiResponse;
import konkuk.ptal.dto.request.CreateReviewCommentRequest;
import konkuk.ptal.dto.response.ReadCommentResponse;
import konkuk.ptal.entity.ReviewComment;
import konkuk.ptal.service.IReviewService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class ReviewCommentController {

    private final IReviewService reviewService;

    @GetMapping("/reviews/{sessionId}/comments")
    public ResponseEntity<ApiResponse<List<ReadCommentResponse>>> getReviewComments(@PathVariable Long sessionId){
        // TODO : 댓글에 대해 접근 권한이 있는지 보는 코드

        List<ReviewComment> reviewComment = reviewService.getReviewComments(sessionId, null);
        return ResponseEntity.ok(ApiResponse.success("댓글 조회 성공", ReadCommentResponse.from(reviewComment)));
    }

    @GetMapping("/reviews/{sessionId}/comments/{fileId}")
    public ResponseEntity<ApiResponse<List<ReadCommentResponse>>> getReviewComments(
            @PathVariable Long sessionId,
            @PathVariable Long fileId
    ){
        // TODO : 댓글에 대해 접근 권한이 있는지 보는 코드

        List<ReviewComment> reviewComment = reviewService.getReviewComments(sessionId, fileId);
        return ResponseEntity.ok(ApiResponse.success("댓글 조회 성공", ReadCommentResponse.from(reviewComment)));
    }

    @PostMapping("/reviews/{sessionId}/comments")
    public ResponseEntity<ApiResponse<ReadCommentResponse>> createReviewComment(
            @PathVariable Long sessionId,
            @RequestBody CreateReviewCommentRequest request
    ){
        // TODO : 댓글에 대해 접근 권한이 있는지 보는 코드

        ReviewComment reviewComment = reviewService.createReviewComment(sessionId, request);
        return ResponseEntity.ok(ApiResponse.success("댓글 작성 성공", ReadCommentResponse.from(reviewComment)));
    }

    @PostMapping("/reviews/{sessionId}/comments/{commentId}")
    public ResponseEntity<ApiResponse<ReadCommentResponse>> createReviewCommentWithParent(
            @PathVariable Long sessionId,
            @PathVariable Long commentId,
            @RequestBody CreateReviewCommentRequest request
    ) {
        // TODO : 댓글에 대해 접근 권한이 있는지 보는 코드
        // TODO : 여기서 parentId를 어떻게 관리할지 보자

        request.setParentCommentId(commentId);
        ReviewComment reviewComment = reviewService.createReviewComment(sessionId, request);
        return ResponseEntity.ok(ApiResponse.success("댓글 작성 성공", ReadCommentResponse.from(reviewComment)));
    }

    @PostMapping("/reviews/{sessionId}/comments/{fileId}")
    public ResponseEntity<ApiResponse<ReadCommentResponse>> createReviewCommentOnFile(
            @PathVariable Long sessionId,
            @PathVariable Long fileId,
            @RequestBody CreateReviewCommentRequest request
    ) {
        // TODO : 댓글에 대해 접근 권한이 있는지 보는 코드
        // TODO : 여기서 fileId를 어떻게 관리할지 보자

        request.setCodeFileId(fileId);
        ReviewComment reviewComment = reviewService.createReviewComment(sessionId, request);
        return ResponseEntity.ok(ApiResponse.success("댓글 작성 성공", ReadCommentResponse.from(reviewComment)));
    }

    // TODO: Put 관련 메서드 구현

    @DeleteMapping("/reviews/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteReviewComment(@PathVariable Long commentId){
        reviewService.deleteReviewComment(commentId);
        return ResponseEntity.ok(ApiResponse.success("댓글 삭제 성공", null));
    }

}
