package konkuk.ptal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    /**
     * RedisTemplate Bean을 등록합니다.
     * 키와 값의 직렬화 방식을 설정하여 Redis에 데이터를 저장하고 조회할 때 문제가 없도록 합니다.
     * StringRedisSerializer: Key를 String으로 직렬화 (가독성 좋음)
     * GenericJackson2JsonRedisSerializer: Value를 JSON 형태로 직렬화 (객체 저장 시 유용)
     *
     * @param redisConnectionFactory Redis 연결 팩토리 (Spring Boot가 자동 설정)
     * @return 설정된 RedisTemplate 객체
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory); // Redis 연결 팩토리 설정

        // Key 직렬화 설정 (String)
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // Value 직렬화 설정 (JSON)
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        // Hash Key 직렬화 설정 (String) - Redis Hash 타입을 사용할 경우
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        // Hash Value 직렬화 설정 (JSON) - Redis Hash 타입을 사용할 경우
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        redisTemplate.afterPropertiesSet(); // 직렬화 설정 후 초기화
        return redisTemplate;
    }
}
