package dev.lqwd.cloudfilestorage.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


@Configuration
@EnableRedisHttpSession
@RequiredArgsConstructor
public class RedisSessionConfig {

    private final JacksonConfig jacksonConfig;

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {

        ObjectMapper mapper = jacksonConfig.objectMapper().copy();

        mapper.registerModules(SecurityJackson2Modules.getModules(getClass().getClassLoader()));
        return new GenericJackson2JsonRedisSerializer(mapper);
    }

}



