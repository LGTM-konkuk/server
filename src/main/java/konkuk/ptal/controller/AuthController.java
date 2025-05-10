package konkuk.ptal.controller;

import jakarta.validation.Valid;
import konkuk.ptal.dto.api.ApiResponse;
import konkuk.ptal.dto.api.ResponseCode;
import konkuk.ptal.dto.request.LoginRequestDto;
import konkuk.ptal.dto.request.SignupRequestDto;
import konkuk.ptal.dto.response.TokenResponseDto;
import konkuk.ptal.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor

public class AuthController {

    private final IAuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignupRequestDto signupRequestDto) {
        authService.register(signupRequestDto);
        return ResponseEntity.ok(ApiResponse.success(ResponseCode.MEMBER_REGISTER_SUCCESS, null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponseDto>> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        TokenResponseDto token = authService.login(loginRequestDto);
        return ResponseEntity.ok(ApiResponse.success(ResponseCode.LOGIN_SUCCESS, token));
    }
}
