package konkuk.ptal.controller;

import konkuk.ptal.domain.UserPrincipal;
import konkuk.ptal.dto.api.ApiResponse;
import konkuk.ptal.dto.response.ReadCommentResponse;
import konkuk.ptal.entity.ReviewComment;
import konkuk.ptal.service.IReviewService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
