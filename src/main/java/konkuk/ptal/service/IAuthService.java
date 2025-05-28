package konkuk.ptal.service;

import konkuk.ptal.dto.request.CreateRevieweeRequest;
import konkuk.ptal.dto.request.CreateReviewerRequest;
import konkuk.ptal.dto.request.UserLoginRequest;
import konkuk.ptal.dto.response.AuthTokenResponse;

public interface IAuthService {
    public AuthTokenResponse login(UserLoginRequest dto);
}
