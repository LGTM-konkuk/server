package konkuk.ptal.service;

import konkuk.ptal.dto.request.RefreshTokenRequest;
import konkuk.ptal.dto.request.UserLoginRequest;
import konkuk.ptal.dto.response.AuthTokenResponse;

public interface IAuthenticationService {
    AuthTokenResponse login(UserLoginRequest dto);

    AuthTokenResponse refreshAccessToken(RefreshTokenRequest dto);
    void logout(String email, String accessToken);
}
