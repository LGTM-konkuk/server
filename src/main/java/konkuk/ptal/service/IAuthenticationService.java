package konkuk.ptal.service;

import konkuk.ptal.dto.request.RefreshTokenRequest;
import konkuk.ptal.dto.request.UserLoginRequest;
import konkuk.ptal.dto.response.AuthTokenResponse;
import org.springframework.security.core.userdetails.UserDetails;

public interface IAuthenticationService {
    public AuthTokenResponse login(UserLoginRequest dto);
    public AuthTokenResponse refreshAccessToken(RefreshTokenRequest dto);

    public void logout(String email);
}
