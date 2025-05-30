package konkuk.ptal.controller;

import jakarta.validation.Valid;
import konkuk.ptal.domain.UserPrincipal;
import konkuk.ptal.dto.api.ApiResponse;
import konkuk.ptal.dto.api.ResponseCode;
import konkuk.ptal.dto.request.*;
import konkuk.ptal.dto.response.CreateRevieweeResponse;
import konkuk.ptal.dto.response.CreateReviewerResponse;
import konkuk.ptal.dto.response.ReadUserResponse;
import konkuk.ptal.entity.Reviewee;
import konkuk.ptal.entity.Reviewer;
import konkuk.ptal.entity.User;
import konkuk.ptal.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1") // Base path for user-specific operations
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @PostMapping("/auth/signup/reviewee")
    public ResponseEntity<ApiResponse<CreateRevieweeResponse>> registerReviewee(
            @Valid @RequestBody CreateRevieweeRequest requestDto) {

        Reviewee reviewee = userService.registerReviewee(requestDto);
        CreateRevieweeResponse responseDto = CreateRevieweeResponse.from(reviewee);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(ResponseCode.REVIEWEE_REGISTER_SUCCESS.getMessage(), responseDto));
    }

    @PostMapping("/auth/signup/reviewer")
    public ResponseEntity<ApiResponse<CreateReviewerResponse>> registerReviewer(
            @Valid @RequestBody CreateReviewerRequest requestDto) {

        Reviewer reviewer = userService.registerReviewer(requestDto);
        CreateReviewerResponse responseDto = CreateReviewerResponse.from(reviewer);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(ResponseCode.REVIEWER_REGISTER_SUCCESS.getMessage(), responseDto));
    }

    @GetMapping("/reviewee/{id}")
    public ResponseEntity<ApiResponse<CreateRevieweeResponse>> getReviewee(@PathVariable Long id) {
        Reviewee reviewee = userService.getReviewee(id);
        CreateRevieweeResponse responseDto = CreateRevieweeResponse.from(reviewee);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(ResponseCode.DATA_RETRIEVED.getMessage(), responseDto));
    }

    @PutMapping("/reviewee/{id}")
    public ResponseEntity<ApiResponse<CreateRevieweeResponse>> updateReviewee(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRevieweeRequest requestDto,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUserId();
        Reviewee updatedReviewee = userService.updateReviewee(id, requestDto, userId);
        CreateRevieweeResponse responseDto = CreateRevieweeResponse.from(updatedReviewee);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(ResponseCode.OK.getMessage(), responseDto));
    }

    @GetMapping("/reviewer/{id}")
    public ResponseEntity<ApiResponse<CreateReviewerResponse>> getReviewer(@PathVariable Long id) {
        Reviewer reviewer = userService.getReviewer(id);
        CreateReviewerResponse responseDto = CreateReviewerResponse.from(reviewer);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(ResponseCode.DATA_RETRIEVED.getMessage(), responseDto));
    }

    @PutMapping("/reviewer/{id}")
    public ResponseEntity<ApiResponse<CreateReviewerResponse>> updateReviewer(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReviewerRequest requestDto,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUserId();
        Reviewer updatedReviewer = userService.updateReviewer(id, requestDto, userId);
        CreateReviewerResponse responseDto = CreateReviewerResponse.from(updatedReviewer);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(ResponseCode.DATA_RETRIEVED.getMessage(), responseDto));
    }

    @GetMapping("/users/me")
    public ResponseEntity<ApiResponse<ReadUserResponse>> getUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUserId();
        User user = userService.getUser(userId);
        ReadUserResponse responseDto = ReadUserResponse.from(user);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(ResponseCode.DATA_RETRIEVED.getMessage(), responseDto));
    }

    @PatchMapping("/users/me")
    public ResponseEntity<ApiResponse<ReadUserResponse>> editUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody UpdateUserRequest request) {
        Long userId = userPrincipal.getUserId();
        User user = userService.updateUser(userId, request);
        ReadUserResponse responseDto = ReadUserResponse.from(user);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(ResponseCode.DATA_UPDATE_SUCCESS.getMessage(), responseDto));
    }

// 추가로 리뷰어목록도 누락돼있는거같은데 자세하게 함 봐야할듯..
// 문서에 있는 List쪽은 다 누락된거같음
}

