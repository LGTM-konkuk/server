package konkuk.ptal.controller;

import jakarta.validation.Valid;
import konkuk.ptal.dto.api.ApiResponse;
import konkuk.ptal.dto.api.ResponseCode;
import konkuk.ptal.dto.request.LoginRequest;
import konkuk.ptal.dto.request.SignupRequest;
import konkuk.ptal.dto.response.TokenResponse;
import konkuk.ptal.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor

public class AuthController {

    private final IAuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignupRequest signupRequest) {
        authService.register(signupRequest);
        return ResponseEntity.ok(ApiResponse.success(ResponseCode.MEMBER_REGISTER_SUCCESS, null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        TokenResponse token = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success(ResponseCode.LOGIN_SUCCESS, token));
    }
}
