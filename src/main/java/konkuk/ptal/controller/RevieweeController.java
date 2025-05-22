package konkuk.ptal.controller;

import jakarta.validation.Valid;
import konkuk.ptal.dto.api.ApiResponse;
import konkuk.ptal.dto.request.CreateRevieweeDto;
import konkuk.ptal.dto.response.ResponseRevieweeDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface RevieweeController {

    ResponseEntity<ApiResponse<ResponseRevieweeDto>> registerReviewee(
            @Valid @RequestBody CreateRevieweeDto requestDto,
            @AuthenticationPrincipal Long userId);

    ResponseEntity<ApiResponse<ResponseRevieweeDto>> getReviewee(@PathVariable Long id);

    ResponseEntity<ApiResponse<ResponseRevieweeDto>> updateReviewee(
            @PathVariable Long id,
            @Valid @RequestBody CreateRevieweeDto requestDto,
            @AuthenticationPrincipal Long userId);
}
