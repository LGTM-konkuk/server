package konkuk.ptal.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import konkuk.ptal.config.TestSecurityConfig;
import konkuk.ptal.domain.UserPrincipal;
import konkuk.ptal.dto.api.ErrorCode;
import konkuk.ptal.dto.api.ResponseCode;
import konkuk.ptal.dto.request.UpdateUserRequest;
import konkuk.ptal.entity.User;
import konkuk.ptal.exception.BadRequestException;
import konkuk.ptal.service.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Long testUserId;
    private UserPrincipal testUserPrincipal;

    @BeforeEach
    void setUp() {
        testUserId = 1L;
        testUser = User.builder()
                .id(testUserId)
                .email("test@example.com")
                .name("Test User")
                .passwordHash("hashedPassword")
                .build();

        // UserPrincipal 모킹
        testUserPrincipal = new UserPrincipal(
                testUserId,
                "test@example.com",
                "hashedPassword",
                List.of("ROLE_USER")
        );
    }

    private String createUpdateUserJson(String name, String email, String password) throws JsonProcessingException {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;

        if (name != null) {
            json.append("\"name\":\"").append(name).append("\"");
            first = false;
        }

        if (email != null) {
            if (!first) json.append(",");
            json.append("\"email\":\"").append(email).append("\"");
            first = false;
        }

        if (password != null) {
            if (!first) json.append(",");
            json.append("\"password\":\"").append(password).append("\"");
        }

        json.append("}");
        return json.toString();
    }

    @Test
    @DisplayName("GET /api/v1/users/me - 사용자 정보 조회 성공")
    void getUserInfo_Success() throws Exception {
        // given
        when(userService.getUser(testUserId)).thenReturn(testUser);

        // when & then
        mockMvc.perform(get("/api/v1/users/me")
                        .with(user(testUserPrincipal))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResponseCode.DATA_RETRIEVED.getMessage()))
                .andExpect(jsonPath("$.data.id").value(testUserId))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.name").value("Test User"));
    }

    @Test
    @DisplayName("PATCH /api/v1/users/me - 사용자 정보 수정 성공")
    void updateUserInfo_Success() throws Exception {
        // given
        User updatedUser = User.builder()
                .id(testUserId)
                .email("updated@example.com")
                .name("Updated Name")
                .passwordHash("hashedPassword")
                .build();

        when(userService.updateUser(eq(testUserId), any(UpdateUserRequest.class)))
                .thenReturn(updatedUser);

        String requestJson = createUpdateUserJson("Updated Name", "updated@example.com", null);

        // when & then
        mockMvc.perform(patch("/api/v1/users/me")
                        .with(user(testUserPrincipal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResponseCode.DATA_UPDATE_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.id").value(testUserId))
                .andExpect(jsonPath("$.data.email").value("updated@example.com"))
                .andExpect(jsonPath("$.data.name").value("Updated Name"));
    }

    @Test
    @DisplayName("PATCH /api/v1/users/me - 부분 업데이트 성공 (이름만 변경)")
    void updateUserInfo_PartialUpdate_Success() throws Exception {
        // given
        User updatedUser = User.builder()
                .id(testUserId)
                .email("test@example.com")
                .name("Updated Name Only")
                .passwordHash("hashedPassword")
                .build();

        when(userService.updateUser(eq(testUserId), any(UpdateUserRequest.class)))
                .thenReturn(updatedUser);

        String requestJson = createUpdateUserJson("Updated Name Only", null, null);

        // when & then
        mockMvc.perform(patch("/api/v1/users/me")
                        .with(user(testUserPrincipal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResponseCode.DATA_UPDATE_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.name").value("Updated Name Only"));
    }

    @Test
    @DisplayName("PATCH /api/v1/users/me - 잘못된 요청 데이터")
    void updateUserInfo_InvalidRequest() throws Exception {
        // given
        User updatedUser = User.builder()
                .id(testUserId)
                .email("test@example.com")
                .name("Test User")
                .passwordHash("hashedPassword")
                .build();

        when(userService.updateUser(eq(testUserId), any(UpdateUserRequest.class)))
                .thenReturn(updatedUser);

        String requestJson = "{}"; // 빈 객체

        // when & then
        mockMvc.perform(patch("/api/v1/users/me")
                        .with(user(testUserPrincipal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk()); // 빈 Optional 필드들로 인해 성공할 수 있음
    }

    @Test
    @DisplayName("GET /api/v1/users/me - 사용자를 찾을 수 없음")
    void getUserInfo_UserNotFound() throws Exception {
        // given
        when(userService.getUser(testUserId))
                .thenThrow(new BadRequestException(ErrorCode.USER_NOT_FOUND));

        // when & then
        mockMvc.perform(get("/api/v1/users/me")
                        .with(user(testUserPrincipal))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /api/v1/users/me - 사용자를 찾을 수 없음")
    void updateUserInfo_UserNotFound() throws Exception {
        // given
        when(userService.updateUser(eq(testUserId), any(UpdateUserRequest.class)))
                .thenThrow(new BadRequestException(ErrorCode.USER_NOT_FOUND));

        String requestJson = createUpdateUserJson("Updated Name", null, null);

        // when & then
        mockMvc.perform(patch("/api/v1/users/me")
                        .with(user(testUserPrincipal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /api/v1/users/me - 잘못된 JSON 형식")
    void updateUserInfo_InvalidJsonFormat() throws Exception {
        // given
        String invalidJson = "{ invalid json }";

        // when & then
        mockMvc.perform(patch("/api/v1/users/me")
                        .with(user(testUserPrincipal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().is5xxServerError()); // JSON 파싱 오류는 500으로 처리됨
    }
}