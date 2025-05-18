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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authService;
    @Mock
    private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private AuthenticationManager authenticationManager;

    @Test
    void registerSuccess() {
        SignupRequestDto dto = new SignupRequestDto();
        dto.setEmail("test@example.com");
        dto.setPassword("123456");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");

        authService.register(dto);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerDuplicateEmail() {
        SignupRequestDto dto = new SignupRequestDto();
        dto.setEmail("dup@example.com");
        dto.setPassword("123456");

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> authService.register(dto));
        assertEquals(ErrorCode.DUPLICATED_EMAIL, ex.getErrorCode());
    }

    @Test
    void loginSuccess() {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("user@example.com");
        dto.setPassword("password");

        User user = User.createUser("user@example.com", "hashed");
        user.setId(1L);

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())).thenReturn(true);

        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtTokenProvider.generateToken(auth)).thenReturn(
                TokenInfo.builder()
                        .grantType("Bearer")
                        .accessToken("access-token")
                        .refreshToken("refresh-token")
                        .build()
        );

        TokenResponseDto response = authService.login(dto);
        assertEquals("access-token", response.getToken());
        assertEquals(1L, response.getUserId());
        assertEquals("user@example.com", response.getEmail());
    }

    @Test
    void loginUserNotFound() {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("notfound@example.com");
        dto.setPassword("pw");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () -> authService.login(dto));
        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void loginPasswordMismatch() {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("user@example.com");
        dto.setPassword("wrong");

        User user = User.createUser("user@example.com", "hashed");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> authService.login(dto));
        assertEquals(ErrorCode.PASSWORD_NOT_EQUAL, ex.getErrorCode());
    }
}