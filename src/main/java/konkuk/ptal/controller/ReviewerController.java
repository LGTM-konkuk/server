package konkuk.ptal.controller;

import jakarta.validation.Valid;
import konkuk.ptal.dto.api.ApiResponse;
import konkuk.ptal.dto.request.CreateReviewerRequestDto;
import konkuk.ptal.dto.response.ReviewerResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface ReviewerController {

    ResponseEntity<ApiResponse<ReviewerResponseDto>> registerReviewer(
            @Valid @RequestBody CreateReviewerRequestDto requestDto,
            @AuthenticationPrincipal Long userId);

    public ResponseEntity<ReviewerResponseDto> getReviewer(@PathVariable Long id);

    public ResponseEntity<ReviewerResponseDto> updateReviewer(
            @PathVariable Long id,
            @Valid @RequestBody CreateReviewerRequestDto requestDto,
            @AuthenticationPrincipal Long userId);

}
