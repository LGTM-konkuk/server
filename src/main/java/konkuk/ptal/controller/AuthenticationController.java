package konkuk.ptal.controller;

import jakarta.validation.Valid;
import konkuk.ptal.dto.api.ApiResponse;
import konkuk.ptal.dto.api.ErrorCode;
import konkuk.ptal.dto.api.ResponseCode;
import konkuk.ptal.dto.request.UserLoginRequest;
import konkuk.ptal.dto.response.AuthTokenResponse;
import konkuk.ptal.exception.UnauthorizedException;
import konkuk.ptal.service.IAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        try{
            AuthTokenResponse token = authService.login(userLoginRequest);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success(ResponseCode.LOGIN_SUCCESS.getMessage(), token));
        } catch (UnauthorizedException e){
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                  .body(ApiResponse.fail(ErrorCode.USER_NOT_FOUND.getMessage() + "또는" + ErrorCode.PASSWORD_NOT_EQUAL.getMessage(), null));
        }
    }
}
