package konkuk.ptal.service;

import konkuk.ptal.dto.request.UpdateUserRequest;
import konkuk.ptal.entity.User;
import konkuk.ptal.exception.BadRequestException;
import konkuk.ptal.repository.RevieweeRepository;
import konkuk.ptal.repository.ReviewerRepository;
import konkuk.ptal.repository.UserRepository;
import konkuk.ptal.util.PasswordEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ReviewerRepository reviewerRepository;

    @Mock
    private RevieweeRepository revieweeRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private Long testUserId;

    @BeforeEach
    void setUp() {
        testUserId = 1L;
        testUser = User.builder()
                .id(testUserId)
                .email("test@example.com")
                .name("Test User")
                .passwordHash("hashedPassword")
                .build();
    }

    @Test
    @DisplayName("getUser - 사용자 조회 성공")
    void getUser_Success() {
        // given
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // when
        User result = userService.getUser(testUserId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testUserId);
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getName()).isEqualTo("Test User");

        verify(userRepository).findById(testUserId);
    }

    @Test
    @DisplayName("getUser - 사용자를 찾을 수 없는 경우 예외 발생")
    void getUser_UserNotFound_ThrowException() {
        // given
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUser(testUserId))
                .isInstanceOf(BadRequestException.class);

        verify(userRepository).findById(testUserId);
    }

    @Test
    @DisplayName("updateUser - 모든 필드 업데이트 성공")
    void updateUser_AllFields_Success() {
        // given
        UpdateUserRequest updateRequest = UpdateUserRequest.builder()
                .email("updated@example.com")
                .name("Updated Name")
                .password("newPassword")
                .build();

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("newPassword")).thenReturn("hashedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // when
        User result = userService.updateUser(testUserId, updateRequest);

        // then
        assertThat(result).isNotNull();
        verify(userRepository).findById(testUserId);
        verify(passwordEncoder).encode("newPassword");
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("updateUser - 이메일만 업데이트 성공")
    void updateUser_EmailOnly_Success() {
        // given
        UpdateUserRequest updateRequest = UpdateUserRequest.builder()
                .email("newemail@example.com")
                .build();

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // when
        User result = userService.updateUser(testUserId, updateRequest);

        // then
        assertThat(result).isNotNull();
        verify(userRepository).findById(testUserId);
        verify(userRepository).save(testUser);
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("updateUser - 이름만 업데이트 성공")
    void updateUser_NameOnly_Success() {
        // given
        UpdateUserRequest updateRequest = UpdateUserRequest.builder()
                .name("New Name")
                .build();

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // when
        User result = userService.updateUser(testUserId, updateRequest);

        // then
        assertThat(result).isNotNull();
        verify(userRepository).findById(testUserId);
        verify(userRepository).save(testUser);
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("updateUser - 비밀번호만 업데이트 성공")
    void updateUser_PasswordOnly_Success() {
        // given
        UpdateUserRequest updateRequest = UpdateUserRequest.builder()
                .password("newPassword123")
                .build();

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("newPassword123")).thenReturn("hashedNewPassword123");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // when
        User result = userService.updateUser(testUserId, updateRequest);

        // then
        assertThat(result).isNotNull();
        verify(userRepository).findById(testUserId);
        verify(passwordEncoder).encode("newPassword123");
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("updateUser - 빈 요청으로 업데이트 (변경사항 없음)")
    void updateUser_EmptyRequest_Success() {
        // given
        UpdateUserRequest updateRequest = UpdateUserRequest.builder().build();

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // when
        User result = userService.updateUser(testUserId, updateRequest);

        // then
        assertThat(result).isNotNull();
        verify(userRepository).findById(testUserId);
        verify(userRepository).save(testUser);
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("updateUser - 사용자를 찾을 수 없는 경우 예외 발생")
    void updateUser_UserNotFound_ThrowException() {
        // given
        UpdateUserRequest updateRequest = UpdateUserRequest.builder()
                .name("New Name")
                .build();

        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.updateUser(testUserId, updateRequest))
                .isInstanceOf(BadRequestException.class);

        verify(userRepository).findById(testUserId);
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("updateUser - null ID로 업데이트 시도")
    void updateUser_NullUserId_ThrowException() {
        // given
        UpdateUserRequest updateRequest = UpdateUserRequest.builder()
                .name("New Name")
                .build();

        when(userRepository.findById(null)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.updateUser(null, updateRequest))
                .isInstanceOf(BadRequestException.class);

        verify(userRepository).findById(null);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("getUser - null ID로 조회 시도")
    void getUser_NullUserId_ThrowException() {
        // given
        when(userRepository.findById(null)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUser(null))
                .isInstanceOf(BadRequestException.class);

        verify(userRepository).findById(null);
    }

    @Test
    @DisplayName("updateUser - 존재하지 않는 사용자 ID로 업데이트 시도")
    void updateUser_NonExistentUserId_ThrowException() {
        // given
        Long nonExistentUserId = 99999L;
        UpdateUserRequest updateRequest = UpdateUserRequest.builder()
                .email("new@example.com")
                .name("Updated Name")
                .password("newPassword")
                .build();

        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.updateUser(nonExistentUserId, updateRequest))
                .isInstanceOf(BadRequestException.class);

        verify(userRepository).findById(nonExistentUserId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("getUser - 존재하지 않는 사용자 ID로 조회 시도")
    void getUser_NonExistentUserId_ThrowException() {
        // given
        Long nonExistentUserId = 99999L;
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUser(nonExistentUserId))
                .isInstanceOf(BadRequestException.class);

        verify(userRepository).findById(nonExistentUserId);
    }

    @Test
    @DisplayName("updateUser - 데이터베이스 저장 중 예외 발생")
    void updateUser_DatabaseSaveException() {
        // given
        UpdateUserRequest updateRequest = UpdateUserRequest.builder()
                .name("Updated Name")
                .password("newPassword")
                .build();

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        // when & then
        assertThatThrownBy(() -> userService.updateUser(testUserId, updateRequest))
                .isInstanceOf(RuntimeException.class);

        verify(userRepository).findById(testUserId);
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("updateUser - 비밀번호 암호화 중 예외 발생")
    void updateUser_PasswordEncodingException() {
        // given
        UpdateUserRequest updateRequest = UpdateUserRequest.builder()
                .name("Updated Name")
                .password("newPassword")
                .build();

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("newPassword")).thenThrow(new RuntimeException("Encoding error"));

        // when & then
        assertThatThrownBy(() -> userService.updateUser(testUserId, updateRequest))
                .isInstanceOf(RuntimeException.class);

        verify(userRepository).findById(testUserId);
        verify(passwordEncoder).encode("newPassword");
        verify(userRepository, never()).save(any(User.class));
    }
}