package konkuk.ptal.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import konkuk.ptal.dto.api.ApiResponse;
import konkuk.ptal.dto.api.ErrorCode;
import konkuk.ptal.dto.api.ResponseCode;
import konkuk.ptal.dto.request.RefreshTokenRequest;
import konkuk.ptal.dto.request.UserLoginRequest;
import konkuk.ptal.dto.response.AuthTokenResponse;
import konkuk.ptal.exception.UnauthorizedException;
import konkuk.ptal.service.IAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor

public class AuthenticationController {

    private final IAuthenticationService authService;

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<AuthTokenResponse>> login(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        try {
            AuthTokenResponse token = authService.login(userLoginRequest);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success(ResponseCode.LOGIN_SUCCESS.getMessage(), token));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail(ErrorCode.USER_NOT_FOUND.getMessage() + "또는 " + ErrorCode.PASSWORD_NOT_EQUAL.getMessage(), null));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthTokenResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {
        try {
            AuthTokenResponse token = authService.refreshAccessToken(request);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success(ResponseCode.TOKEN_REFRESH_SUCCESS.getMessage(), token));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail(ErrorCode.INVALID_JWT.getMessage(), null));
        }
    }

    @PostMapping("/signout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request
    ) {
        try {
            String accessToken = extractAccessToken(request);
            authService.logout(userDetails.getUsername(), accessToken);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success(ResponseCode.LOGOUT_SUCCESS.getMessage(), null));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail(ErrorCode.USER_NOT_FOUND.getMessage(), null));
        }
    }

    private String extractAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
