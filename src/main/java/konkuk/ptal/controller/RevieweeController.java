package konkuk.ptal.controller;

import jakarta.validation.Valid;
import konkuk.ptal.dto.api.ApiResponse;
import konkuk.ptal.dto.api.ResponseCode;
import konkuk.ptal.dto.request.CreateRevieweeDto;
import konkuk.ptal.dto.response.ResponseRevieweeDto;
import konkuk.ptal.entity.Reviewee;
import konkuk.ptal.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviewee")
@RequiredArgsConstructor
public class RevieweeController {

    private final IUserService IUserService;

    @PostMapping
    public ResponseEntity<ApiResponse<ResponseRevieweeDto>> registerReviewee(
            @Valid @RequestBody CreateRevieweeDto requestDto,
            @AuthenticationPrincipal Long userId) {

        Reviewee reviewee = IUserService.registerReviewee(requestDto, userId);
        ResponseRevieweeDto responseDto = ResponseRevieweeDto.from(reviewee);

        return ResponseEntity.ok(ApiResponse.success(ResponseCode.REVIEWEE_REGISTER_SUCCESS, responseDto));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ResponseRevieweeDto>> getReviewee(@PathVariable Long id) {
        Reviewee reviewee = IUserService.getReviewee(id);
        ResponseRevieweeDto responseDto = ResponseRevieweeDto.from(reviewee);
        return ResponseEntity.ok(ApiResponse.success(ResponseCode.DATA_RETRIEVED, responseDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ResponseRevieweeDto>> updateReviewee(
            @PathVariable Long id,
            @Valid @RequestBody CreateRevieweeDto requestDto,
            @AuthenticationPrincipal Long userId) {
        Reviewee updatedReviewee = IUserService.updateReviewee(id, requestDto, userId);
        ResponseRevieweeDto responseDto = ResponseRevieweeDto.from(updatedReviewee);
        return ResponseEntity.ok(ApiResponse.success(ResponseCode.OK, responseDto));
    }
}
