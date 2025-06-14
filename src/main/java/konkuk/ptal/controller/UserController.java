package konkuk.ptal.controller;

import jakarta.validation.Valid;
import konkuk.ptal.domain.UserPrincipal;
import konkuk.ptal.dto.api.ApiResponse;
import konkuk.ptal.dto.api.ResponseCode;
import konkuk.ptal.dto.request.*;
import konkuk.ptal.dto.response.CreateRevieweeResponse;
import konkuk.ptal.dto.response.CreateReviewerResponse;
import konkuk.ptal.dto.response.ListReviewersResponse;
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

import java.util.Arrays;
import java.util.List;

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

    @GetMapping("/reviewers")
    public ResponseEntity<ApiResponse<ListReviewersResponse>> getReviewers(
            @RequestParam(value = "preferences", required = false) String preferences,
            @RequestParam(value = "tags", required = false) String tags,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        
        // 쉼표로 구분된 문자열을 List로 변환
        List<String> preferencesList = null;
        if (preferences != null && !preferences.trim().isEmpty()) {
            preferencesList = Arrays.asList(preferences.split(","));
        }
        
        List<String> tagsList = null;
        if (tags != null && !tags.trim().isEmpty()) {
            tagsList = Arrays.asList(tags.split(","));
        }
        
        ListReviewersResponse responseDto = userService.getReviewers(preferencesList, tagsList, page, size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(ResponseCode.DATA_RETRIEVED.getMessage(), responseDto));
    }
}

