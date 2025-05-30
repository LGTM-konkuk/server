package konkuk.ptal.config; // 패키지 경로는 실제 프로젝트에 맞게

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;
    private final AntPathMatcher pathMatcher = new AntPathMatcher(); // 경로 매칭을 위해 추가

    // SecurityConfig에서 정의한 SWAGGER_PATHS를 여기서도 사용
    private static final String[] DEFAULT_PERMIT_PATHS = {
            "/api/v1/auth/signup/reviewer",
            "/api/v1/auth/signup/reviewee",
            "/api/v1/auth/signin",
            "/h2-console/**"
            // 다른 기본 허용 경로가 있다면 추가
    };


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getRequestURI();

        // Swagger 경로 및 기본 허용 경로 확인
        boolean isPermittedPath = Arrays.stream(SecurityConfig.SWAGGER_PATHS)
                .anyMatch(pattern -> pathMatcher.match(pattern, path)) ||
                Arrays.stream(DEFAULT_PERMIT_PATHS)
                        .anyMatch(pattern -> pathMatcher.match(pattern, path));

        if (isPermittedPath) {
            log.debug("Permitting request to path: {}", path);
            chain.doFilter(request, response);
            return;
        }

        String token = resolveToken(httpRequest);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("인증 성공: 사용자 = {}, 경로 = {}", authentication.getName(), path);
        } else {
            log.warn("유효하지 않은 또는 누락된 JWT 토큰. 경로: {}", path);
            // 401 Unauthorized 응답을 직접 반환
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("application/json;charset=UTF-8");
            // 간결한 에러 메시지 또는 표준 에러 DTO 사용 권장
            httpResponse.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"유효하지 않거나 누락된 토큰입니다.\"}");
            return;  // 필터 체인 종료
        }

        chain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) { // "Bearer " 뒤에 공백 주의
            return bearerToken.substring(7);
        }
        return null;
    }
}