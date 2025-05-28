package konkuk.ptal.service;

import konkuk.ptal.config.JwtTokenProvider;
import konkuk.ptal.domain.TokenInfo;
import konkuk.ptal.dto.api.ErrorCode;
import konkuk.ptal.dto.request.LoginRequest;
import konkuk.ptal.dto.request.SignupRequest;
import konkuk.ptal.dto.response.TokenResponse;
import konkuk.ptal.entity.User;
import konkuk.ptal.exception.BadRequestException;
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
public class AuthServiceImpl implements IAuthService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    // 회원가입
    public void register(SignupRequest signupRequest) {
        log.debug("회원가입 시작: email={}", signupRequest.getEmail());

        // 이메일 중복 체크
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            log.error("회원가입 실패: 이메일 중복, email={}", signupRequest.getEmail());
            throw new BadRequestException(ErrorCode.DUPLICATED_EMAIL);
        }

        String hashedPassword = passwordEncoder.encode(signupRequest.getPassword());
        log.debug("비밀번호 해싱 완료: email={}", signupRequest.getEmail());

        User user = User.createUser(signupRequest.getEmail(), hashedPassword);
        userRepository.save(user);

        log.debug("회원가입 완료: email={}", signupRequest.getEmail());
    }

    // 로그인
    public TokenResponse login(LoginRequest loginRequest) {
        log.debug("로그인 시작: email={}", loginRequest.getEmail());

        // 사용자 존재 여부 체크
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> {
                    log.error("로그인 실패: 사용자 없음, email={}", loginRequest.getEmail());
                    return new UnauthorizedException(ErrorCode.USER_NOT_FOUND);
                });

        // 비밀번호 검증
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            log.error("로그인 실패: 비밀번호 불일치, email={}", loginRequest.getEmail());
            throw new BadRequestException(ErrorCode.PASSWORD_NOT_EQUAL);
        }

        // 인증 객체 생성
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        log.debug("인증 성공: email={}", loginRequest.getEmail());

        // JWT 토큰 생성
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

        log.debug("토큰 생성 완료: email={}, token={}", loginRequest.getEmail(), tokenInfo.getAccessToken());

        // 응답 반환
        return TokenResponse.of(
                tokenInfo.getAccessToken(),
                user.getId(),
                user.getEmail()
        );
    }
}
