package konkuk.ptal.service;

import io.jsonwebtoken.Claims;
import konkuk.ptal.config.JwtTokenProvider;
import konkuk.ptal.domain.TokenInfo;
import konkuk.ptal.dto.api.ErrorCode;
import konkuk.ptal.dto.request.RefreshTokenRequest;
import konkuk.ptal.dto.request.UserLoginRequest;
import konkuk.ptal.dto.response.AuthTokenResponse;
import konkuk.ptal.entity.User;
import konkuk.ptal.exception.UnauthorizedException;
import konkuk.ptal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements IAuthenticationService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;

    public AuthTokenResponse login(UserLoginRequest userLoginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userLoginRequest.getEmail(),
                            userLoginRequest.getPassword()
                    )
            );

            User user = userRepository.findByEmail(userLoginRequest.getEmail())
                    .orElseThrow(() -> {
                        return new UnauthorizedException(ErrorCode.USER_NOT_FOUND);
                    });

            TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);
            user.updateRefreshToken(tokenInfo.getRefreshToken());
            userRepository.save(user);

            return AuthTokenResponse.of(
                    tokenInfo.getAccessToken(),
                    tokenInfo.getRefreshToken()
            );
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException(ErrorCode.PASSWORD_NOT_EQUAL);
        }

    }

    @Override
    public AuthTokenResponse refreshAccessToken(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new UnauthorizedException(ErrorCode.INVALID_JWT);
        }

        Claims claims = jwtTokenProvider.parseClaims(refreshToken);
        String userEmail = claims.getSubject();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> {
                    return new UnauthorizedException(ErrorCode.USER_NOT_FOUND);
                });

        if (user.getRefreshToken() == null || !user.getRefreshToken().equals(refreshToken)) {
            throw new UnauthorizedException(ErrorCode.INVALID_JWT);
        }
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        TokenInfo newTokenInfo = jwtTokenProvider.generateToken(newAuthentication);

        user.updateRefreshToken(newTokenInfo.getRefreshToken());
        userRepository.save(user);

        return AuthTokenResponse.of(newTokenInfo.getAccessToken(), newTokenInfo.getRefreshToken());

    }

    @Override
    @Transactional
    public void logout(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UnauthorizedException(ErrorCode.USER_NOT_FOUND));
        user.clearRefreshToken();
        userRepository.save(user);
    }
}

