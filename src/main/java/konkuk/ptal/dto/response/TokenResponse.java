package konkuk.ptal.dto.response;

import lombok.Data;

@Data
public class TokenResponse {
    private String token;
    private Long userId;
    private String email;

    public static TokenResponse of(String token, Long userId, String email) {
        TokenResponse dto = new TokenResponse();
        dto.setToken(token);
        dto.setUserId(userId);
        dto.setEmail(email);
        return dto;
    }
}
