package konkuk.ptal.service;

import konkuk.ptal.config.JwtTokenProvider;
import konkuk.ptal.domain.TokenInfo;
import konkuk.ptal.dto.api.ErrorCode;
import konkuk.ptal.dto.request.UserLoginRequest;
import konkuk.ptal.dto.response.AuthTokenResponse;
import konkuk.ptal.entity.User;
import konkuk.ptal.exception.UnauthorizedException;
import konkuk.ptal.repository.UserRepository;
import konkuk.ptal.util.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements IAuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    // 로그인
    public AuthTokenResponse login(UserLoginRequest userLoginRequest) {
        User user = userRepository.findByEmail(userLoginRequest.getEmail())
                .orElseThrow(() -> {
                    return new UnauthorizedException(ErrorCode.USER_NOT_FOUND);
                });

        if (!passwordEncoder.matches(userLoginRequest.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException(ErrorCode.PASSWORD_NOT_EQUAL);
        }

        // 인증 객체 생성
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userLoginRequest.getEmail(),
                        userLoginRequest.getPassword()
                )
        );

        // JWT 토큰 생성
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);
        // 응답 반환
        return AuthTokenResponse.of(
                tokenInfo.getAccessToken(),
                tokenInfo.getRefreshToken()
        );
    }
}
