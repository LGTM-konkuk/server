package konkuk.ptal.controller;

import jakarta.validation.Valid;
import konkuk.ptal.domain.UserPrincipal;
import konkuk.ptal.dto.api.ApiResponse;
import konkuk.ptal.dto.api.ResponseCode;
import konkuk.ptal.dto.request.CreateReviewRequest;
import konkuk.ptal.dto.request.UpdateReviewRequest;
import konkuk.ptal.dto.response.ListReviewsResponse;
import konkuk.ptal.dto.response.ProjectFileSystemResponse;
import konkuk.ptal.dto.response.ReadReviewResponse;
import konkuk.ptal.entity.Review;
import konkuk.ptal.entity.ReviewSubmission;
import konkuk.ptal.service.IFileService;
import konkuk.ptal.service.IReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")

public class ReviewController {
    private final IReviewService reviewService;
    private final IFileService fileService;

    @PostMapping("/review-submissions/{submissionId}/reviews")
    public ResponseEntity<ApiResponse<ReadReviewResponse>> createReview(
            @PathVariable("submissionId") Long submissionId,
            @Valid @RequestBody CreateReviewRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        request.setReviewSubmissionId(submissionId);
        Review review = reviewService.createReview(submissionId, request, userPrincipal);
        ReviewSubmission reviewSubmission = review.getReviewSubmission();
        ProjectFileSystemResponse fileSystem = fileService.getProjectFileSystem(reviewSubmission.getGitUrl(), reviewSubmission.getBranch(), reviewSubmission.getId());
        ReadReviewResponse responseDto = ReadReviewResponse.from(review, fileSystem);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(ResponseCode.REVIEW_CREATED.getMessage(), responseDto));
    }

    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<ReadReviewResponse>> getReviewById(
            @PathVariable("reviewId") Long reviewId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        Review review = reviewService.getReview(reviewId, userPrincipal);
        ReviewSubmission reviewSubmission = review.getReviewSubmission();
        ProjectFileSystemResponse fileSystem = fileService.getProjectFileSystem(reviewSubmission.getGitUrl(), reviewSubmission.getBranch(), reviewSubmission.getId());
        ReadReviewResponse responseDto = ReadReviewResponse.from(review, fileSystem);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(ResponseCode.DATA_RETRIEVED.getMessage(), responseDto));
    }

    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<ReadReviewResponse>> updateReview(
            @PathVariable("reviewId") Long reviewId,
            @Valid @RequestBody UpdateReviewRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        Review updatedReview = reviewService.updateReview(reviewId, request, userPrincipal);
        ReviewSubmission reviewSubmission = updatedReview.getReviewSubmission();
        ProjectFileSystemResponse fileSystem = fileService.getProjectFileSystem(reviewSubmission.getGitUrl(), reviewSubmission.getBranch(), reviewSubmission.getId());
        ReadReviewResponse responseDto = ReadReviewResponse.from(updatedReview, fileSystem);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(ResponseCode.REVIEW_UPDATED.getMessage(), responseDto));
    }

    @GetMapping("/reviews")
    public ResponseEntity<ApiResponse<ListReviewsResponse>> getReviews(
            @RequestParam(name = "submissionId", required = false) Long submissionId,
            @RequestParam(name = "reviewerId", required = false) Long reviewerId,
            @RequestParam(name = "revieweeId", required = false) Long revieweeId,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        ListReviewsResponse responseDtos = reviewService.getReviews(submissionId, reviewerId, revieweeId, page, size, userPrincipal);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(ResponseCode.DATA_RETRIEVED.getMessage(), responseDtos));
    }
}
