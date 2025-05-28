package konkuk.ptal.controller;

import jakarta.validation.Valid;
import konkuk.ptal.dto.api.ApiResponse;
import konkuk.ptal.dto.api.ResponseCode;
import konkuk.ptal.dto.request.CreateReviewerRequest;
import konkuk.ptal.dto.response.ReviewerResponse;
import konkuk.ptal.entity.Reviewer;
import konkuk.ptal.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviewer")
@RequiredArgsConstructor
public class ReviewerController {

    private final IUserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewerResponse>> registerReviewer(
            @Valid @RequestBody CreateReviewerRequest requestDto,
            @AuthenticationPrincipal Long userId) {

        Reviewer reviewer = userService.registerReviewer(requestDto, userId);
        ReviewerResponse responseDto = ReviewerResponse.from(reviewer);

        return ResponseEntity.ok(ApiResponse.success(ResponseCode.REVIEWER_REGISTER_SUCCESS, responseDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReviewerResponse>> getReviewer(@PathVariable Long id) {
        Reviewer reviewer = userService.getReviewer(id);
        ReviewerResponse responseDto = ReviewerResponse.from(reviewer);
        return ResponseEntity.ok(ApiResponse.success(ResponseCode.DATA_RETRIEVED, responseDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReviewerResponse>> updateReviewer(
            @PathVariable Long id,
            @Valid @RequestBody CreateReviewerRequest requestDto,
            @AuthenticationPrincipal Long userId) {
        Reviewer updatedReviewer = userService.updateReviewer(id, requestDto, userId);
        ReviewerResponse responseDto = ReviewerResponse.from(updatedReviewer);
        return ResponseEntity.ok(ApiResponse.success(ResponseCode.OK, responseDto));
    }

}

