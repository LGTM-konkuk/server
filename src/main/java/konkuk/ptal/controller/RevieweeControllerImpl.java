package konkuk.ptal.controller;

import jakarta.validation.Valid;
import konkuk.ptal.dto.api.ApiResponse;
import konkuk.ptal.dto.api.ResponseCode;
import konkuk.ptal.dto.request.CreateRevieweeDto;
import konkuk.ptal.dto.response.ResponseRevieweeDto;
import konkuk.ptal.entity.Reviewee;
import konkuk.ptal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviewee")
@RequiredArgsConstructor
public class RevieweeControllerImpl implements RevieweeController {

    private final UserService userService;

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<ResponseRevieweeDto>> registerReviewee(
            @Valid @RequestBody CreateRevieweeDto requestDto,
            @AuthenticationPrincipal Long userId) {

        Reviewee reviewee = userService.registerReviewee(requestDto, userId);
        ResponseRevieweeDto responseDto = ResponseRevieweeDto.from(reviewee);

        return ResponseEntity.ok(ApiResponse.success(ResponseCode.REVIEWEE_REGISTER_SUCCESS, responseDto));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ResponseRevieweeDto>> getReviewee(@PathVariable Long id) {
        Reviewee reviewee = userService.getReviewee(id);
        ResponseRevieweeDto responseDto = ResponseRevieweeDto.from(reviewee);
        return ResponseEntity.ok(ApiResponse.success(ResponseCode.DATA_RETRIEVED, responseDto));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ResponseRevieweeDto>> updateReviewee(
            @PathVariable Long id,
            @Valid @RequestBody CreateRevieweeDto requestDto,
            @AuthenticationPrincipal Long userId) {
        Reviewee updatedReviewee = userService.updateReviewee(id, requestDto, userId);
        ResponseRevieweeDto responseDto = ResponseRevieweeDto.from(updatedReviewee);
        return ResponseEntity.ok(ApiResponse.success(ResponseCode.OK, responseDto));
    }
}
