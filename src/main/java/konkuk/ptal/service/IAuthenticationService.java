package konkuk.ptal.service;

import konkuk.ptal.dto.request.UserLoginRequest;
import konkuk.ptal.dto.response.AuthTokenResponse;

public interface IAuthenticationService {
    public AuthTokenResponse login(UserLoginRequest dto);
}
