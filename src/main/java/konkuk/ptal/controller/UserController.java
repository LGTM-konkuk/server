package konkuk.ptal.controller;

import jakarta.validation.Valid;
import konkuk.ptal.dto.api.ApiResponse;
import konkuk.ptal.dto.api.ResponseCode;
// TODO: Define these DTOs
// import konkuk.ptal.dto.request.UserUpdateRequestDto;
// import konkuk.ptal.dto.response.UserResponseDto;
import konkuk.ptal.entity.User; // Assuming you might return User or a UserResponseDto
import konkuk.ptal.service.IUserService; // Assuming IUserService has general user methods
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users") // Base path for user-specific operations
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService; // Or a dedicated IUserService for general user ops

    // Get current authenticated user's profile
    @GetMapping("/me")
    public ResponseEntity<ApiResponse</*TODO: UserResponseDto*/Object>> getMyProfile(
            @AuthenticationPrincipal Long userId) {
        // User user = userService.getUserProfile(userId);
        // UserResponseDto responseDto = UserResponseDto.from(user);
        // return ResponseEntity.ok(ApiResponse.success(ResponseCode.DATA_RETRIEVED, responseDto));
        // TODO: Implement service and DTO
        return ResponseEntity.ok(ApiResponse.success(ResponseCode.DATA_RETRIEVED, "User profile for ID: " + userId + " - (TODO: Implement)"));
    }

    // Update current authenticated user's profile (e.g., non-role specific info)
    @PutMapping("/me")
    public ResponseEntity<ApiResponse</*TODO: UserResponseDto*/Object>> updateMyProfile(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody /*TODO: UserUpdateRequestDto*/ Object requestDto) {
        // User updatedUser = userService.updateUserProfile(userId, requestDto);
        // UserResponseDto responseDto = UserResponseDto.from(updatedUser);
        // return ResponseEntity.ok(ApiResponse.success(ResponseCode.OK, responseDto));
        // TODO: Implement service and DTO
        return ResponseEntity.ok(ApiResponse.success(ResponseCode.OK, "User profile updated for ID: " + userId + " - (TODO: Implement)"));
    }

}