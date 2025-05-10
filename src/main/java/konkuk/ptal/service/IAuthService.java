package konkuk.ptal.service;

import konkuk.ptal.dto.request.LoginRequestDto;
import konkuk.ptal.dto.request.SignupRequestDto;
import konkuk.ptal.dto.response.TokenResponseDto;

public interface IAuthService {
    public TokenResponseDto login(LoginRequestDto dto);
    public void register(SignupRequestDto signupRequestDto);

}
