package dev.lqwd.cloudfilestorage;

import jakarta.servlet.http.Cookie;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;

@SpringBootTest
@Testcontainers
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
public class TestRedisProvider {

    public static final String REDIS_HASH_TEMPLATE = "spring:session:sessions:";
    public static final String SESSION_COOKIE = "SESSION";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void clearBd(){
        Objects.requireNonNull(redisTemplate.getConnectionFactory())
                .getConnection()
                .serverCommands()
                .flushDb();
    }

    public @NotNull String getSessionId(Cookie[] cookies) {
        Cookie sessionCookie = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(SESSION_COOKIE))
                .findFirst()
                .orElseThrow();

        return new String(Base64.getDecoder().decode(sessionCookie.getValue()), StandardCharsets.UTF_8);
    }

    public void validateRedisSavedSession(String sessionId) {
        Map<Object, Object> retrievedValue = redisTemplate.opsForHash().entries(REDIS_HASH_TEMPLATE + sessionId);
        Assertions.assertFalse(retrievedValue.isEmpty());
    }

    public void validateRedisDontHaveSession(String sessionId) {
        Map<Object, Object> retrievedValue = redisTemplate.opsForHash().entries(REDIS_HASH_TEMPLATE + sessionId);
        Assertions.assertTrue(retrievedValue.isEmpty());
    }
}
