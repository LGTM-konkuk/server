package konkuk.ptal.config; // 패키지 경로는 실제 프로젝트에 맞게

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import konkuk.ptal.service.TokenBlacklistService;
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
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistService tokenBlacklistService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final String[] DEFAULT_PERMIT_PATHS = {
            "/api/v1/auth/signup/reviewer",
            "/api/v1/auth/signup/reviewee",
            "/api/v1/auth/signin",
            // "/api/v1/auth/refresh", // 여기서 제거! SecurityConfig에서 authenticated()로 처리해야 함.
            "/h2-console/**"
    };

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, TokenBlacklistService tokenBlacklistService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getRequestURI();

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

        if (token != null) {
            if (!jwtTokenProvider.validateToken(token)) {
                log.warn("유효하지 않거나 만료된 JWT 토큰. 경로: {}", path);
                sendUnauthorizedResponse(httpResponse, "유효하지 않거나 만료된 토큰입니다.");
                return;
            }

            if (tokenBlacklistService.isTokenBlacklisted(token)) {
                log.warn("블랙리스트에 등록된 JWT 토큰. 경로: {}", path);
                sendUnauthorizedResponse(httpResponse, "블랙리스트에 등록된 토큰입니다. 다시 로그인 해주세요.");
                return;
            }

            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("인증 성공: 사용자 = {}, 경로 = {}", authentication.getName(), path);
        } else {
            log.warn("JWT 토큰이 누락되었습니다. 경로: {}", path);
            sendUnauthorizedResponse(httpResponse, "JWT 토큰이 누락되었습니다.");
            return;
        }

        chain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(String.format("{\"error\": \"Unauthorized\", \"message\": \"%s\"}", message));
    }
}