package konkuk.ptal.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import konkuk.ptal.dto.api.ErrorCode;
import konkuk.ptal.exception.UnauthorizedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        // 디폴트 오류 처리 설정
        ErrorCode errorCode = ErrorCode.INVALID_JWT;
        String message = "잘못된 AccessToken 입니다";

        // 예외가 있으면 커스터마이징된 메시지 사용
        UnauthorizedException exception = (UnauthorizedException) request.getAttribute("exception");
        if (exception != null) {
            errorCode = exception.getErrorCode();
            message = exception.getMessage();
        }

        setResponse(response, errorCode, message);
    }

    private void setResponse(HttpServletResponse response, ErrorCode errorCode, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().println(
                "{" +
                        "\"errorCode\" : \"" + errorCode.getCode() + "\"," +
                        "\"message\" : \"" + message + "\"," +
                        "\"timeStamp\" : \"" + LocalDateTime.now() + "\"" +
                        "}"
        );
    }
}