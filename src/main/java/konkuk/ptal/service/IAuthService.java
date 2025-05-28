package konkuk.ptal.service;

import konkuk.ptal.dto.request.LoginRequest;
import konkuk.ptal.dto.request.SignupRequest;
import konkuk.ptal.dto.response.TokenResponse;

public interface IAuthService {
    public TokenResponse login(LoginRequest dto);
    public void register(SignupRequest signupRequest);

}
