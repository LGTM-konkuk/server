package konkuk.ptal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import konkuk.ptal.dto.request.LoginRequestDto;
import konkuk.ptal.dto.request.SignupRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    void signupSuccessIntegration() throws Exception {
        SignupRequestDto dto = new SignupRequestDto();
        dto.setEmail("integration@example.com");
        dto.setPassword("123456");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("회원가입 성공"));
    }

    @Test
    void signupFailureValidation() throws Exception {
        SignupRequestDto dto = new SignupRequestDto();
        dto.setEmail("not-an-email");
        dto.setPassword("123"); // too short

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void loginSuccessIntegration() throws Exception {
        // 1. 먼저 회원가입을 선행하여 유저를 등록합니다.
        SignupRequestDto signupDto = new SignupRequestDto();
        signupDto.setEmail("logintest@example.com");
        signupDto.setPassword("123456");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupDto)))
                .andExpect(status().isOk());

        // 2. 로그인 요청
        LoginRequestDto loginDto = new LoginRequestDto();
        loginDto.setEmail("logintest@example.com");
        loginDto.setPassword("123456");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.result.token").exists())
                .andExpect(jsonPath("$.result.email").value("logintest@example.com"));
    }

    @Test
    void loginFailureValidation() throws Exception {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("");
        dto.setPassword("");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }
}