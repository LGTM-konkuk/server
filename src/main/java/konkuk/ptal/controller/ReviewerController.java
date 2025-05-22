package konkuk.ptal.controller;

import jakarta.validation.Valid;
import konkuk.ptal.dto.api.ApiResponse;
import konkuk.ptal.dto.api.ResponseCode;
import konkuk.ptal.dto.request.CreateReviewerRequestDto;
import konkuk.ptal.dto.response.ReviewerResponseDto;
import konkuk.ptal.entity.Reviewer;
import konkuk.ptal.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviewer")
@RequiredArgsConstructor
public class ReviewerController {

    private final IUserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewerResponseDto>> registerReviewer(
            @Valid @RequestBody CreateReviewerRequestDto requestDto,
            @AuthenticationPrincipal Long userId) {

        Reviewer reviewer = userService.registerReviewer(requestDto, userId);
        ReviewerResponseDto responseDto = ReviewerResponseDto.from(reviewer);

        return ResponseEntity.ok(ApiResponse.success(ResponseCode.REVIEWER_REGISTER_SUCCESS, responseDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReviewerResponseDto>> getReviewer(@PathVariable Long id) {
        Reviewer reviewer = userService.getReviewer(id);
        ReviewerResponseDto responseDto = ReviewerResponseDto.from(reviewer);
        return ResponseEntity.ok(ApiResponse.success(ResponseCode.DATA_RETRIEVED, responseDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReviewerResponseDto>> updateReviewer(
            @PathVariable Long id,
            @Valid @RequestBody CreateReviewerRequestDto requestDto,
            @AuthenticationPrincipal Long userId) {
        Reviewer updatedReviewer = userService.updateReviewer(id, requestDto, userId);
        ReviewerResponseDto responseDto = ReviewerResponseDto.from(updatedReviewer);
        return ResponseEntity.ok(ApiResponse.success(ResponseCode.OK, responseDto));
    }

}

