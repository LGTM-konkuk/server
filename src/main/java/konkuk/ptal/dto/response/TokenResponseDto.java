package konkuk.ptal.dto.response;

import lombok.Data;

@Data
public class TokenResponseDto {
    private String token;
    private Long userId;
    private String email;

    public static TokenResponseDto of(String token, Long userId, String email) {
        TokenResponseDto dto = new TokenResponseDto();
        dto.setToken(token);
        dto.setUserId(userId);
        dto.setEmail(email);
        return dto;
    }
}
