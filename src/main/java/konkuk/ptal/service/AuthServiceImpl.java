package konkuk.ptal.service;

import konkuk.ptal.config.JwtTokenProvider;
import konkuk.ptal.domain.TokenInfo;
import konkuk.ptal.dto.api.ErrorCode;
import konkuk.ptal.dto.request.LoginRequestDto;
import konkuk.ptal.dto.request.SignupRequestDto;
import konkuk.ptal.dto.response.TokenResponseDto;
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
    public void register(SignupRequestDto signupRequestDto) {
        log.debug("회원가입 시작: email={}", signupRequestDto.getEmail());

        // 이메일 중복 체크
        if (userRepository.existsByEmail(signupRequestDto.getEmail())) {
            log.error("회원가입 실패: 이메일 중복, email={}", signupRequestDto.getEmail());
            throw new BadRequestException(ErrorCode.DUPLICATED_EMAIL);
        }

        String hashedPassword = passwordEncoder.encode(signupRequestDto.getPassword());
        log.debug("비밀번호 해싱 완료: email={}", signupRequestDto.getEmail());

        User user = User.createUser(signupRequestDto.getEmail(), hashedPassword);
        userRepository.save(user);

        log.debug("회원가입 완료: email={}", signupRequestDto.getEmail());
    }

    // 로그인
    public TokenResponseDto login(LoginRequestDto loginRequestDto) {
        log.debug("로그인 시작: email={}", loginRequestDto.getEmail());

        // 사용자 존재 여부 체크
        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> {
                    log.error("로그인 실패: 사용자 없음, email={}", loginRequestDto.getEmail());
                    return new UnauthorizedException(ErrorCode.USER_NOT_FOUND);
                });

        // 비밀번호 검증
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPasswordHash())) {
            log.error("로그인 실패: 비밀번호 불일치, email={}", loginRequestDto.getEmail());
            throw new BadRequestException(ErrorCode.PASSWORD_NOT_EQUAL);
        }

        // 인증 객체 생성
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getEmail(),
                        loginRequestDto.getPassword()
                )
        );

        log.debug("인증 성공: email={}", loginRequestDto.getEmail());

        // JWT 토큰 생성
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

        log.debug("토큰 생성 완료: email={}, token={}", loginRequestDto.getEmail(), tokenInfo.getAccessToken());

        // 응답 반환
        return TokenResponseDto.of(
                tokenInfo.getAccessToken(),
                user.getId(),
                user.getEmail()
        );
    }
}
