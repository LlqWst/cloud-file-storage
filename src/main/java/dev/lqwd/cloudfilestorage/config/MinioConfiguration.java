package dev.lqwd.cloudfilestorage.config;


import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@Slf4j
public class MinioConfiguration {

    @Bean
    public MinioClient minioClient(@Value("${MINIO_ENDPOINT}") String endpoint,
                                   @Value("${MINIO_ADMIN}") String accessKey,
                                   @Value("${MINIO_PASS}") String secretKey) {

        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

}
