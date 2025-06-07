package konkuk.ptal.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.concurrent.TimeUnit;

@Service
public class TokenBlacklistService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final String BLACKLIST_PREFIX = "jwt:blacklist:";
    private final Key secretKey;

    public TokenBlacklistService(RedisTemplate<String, Object> redisTemplate,
                                 @Value("${jwt.secret}") String secretKeyString) {
        this.redisTemplate = redisTemplate;
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyString);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public void addTokenToBlacklist(String token, long expirationTimeMillis) {
        String key = BLACKLIST_PREFIX + token;

        long currentTimeMillis = System.currentTimeMillis();
        long remainingExpirationTimeSeconds = (expirationTimeMillis - currentTimeMillis) / 1000;

        if (remainingExpirationTimeSeconds > 0) {
            redisTemplate.opsForValue().set(key, "blacklisted", remainingExpirationTimeSeconds, TimeUnit.SECONDS);
            System.out.println("블랙리스트에 토큰 추가됨: " + key + ", 남은 시간(초): " + remainingExpirationTimeSeconds);
        } else {
            System.out.println("만료된 토큰이므로 블랙리스트에 추가하지 않음: " + key);
        }
    }

    public boolean isTokenBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    private String extractJtiFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getId();
        } catch (Exception e) {
            System.out.println("토큰에서 Jti 추출 실패");
            return null;
        }

    }
}
