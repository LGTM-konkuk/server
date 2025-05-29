package konkuk.ptal.dto.response;

import lombok.Data;

@Data
public class AuthTokenResponse {
    private String accessToken;
    private String refreshToken;

    public static AuthTokenResponse of(String accessToken, String refreshToken) {
        AuthTokenResponse dto = new AuthTokenResponse();
        dto.setAccessToken(accessToken);
        dto.setRefreshToken(refreshToken);
        return dto;
    }
}
