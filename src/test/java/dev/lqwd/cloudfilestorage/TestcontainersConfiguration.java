package dev.lqwd.cloudfilestorage;

import io.minio.MinioClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    public static final String MINIO_USER = "minioadmin";
    public static final String MINIO_PASS = "minioadmin";

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
                .withDatabaseName("testdb")
                .withUsername("testuser")
                .withPassword("testpass");

    }

    @Bean
    @ServiceConnection(name = "redis")
    GenericContainer<?> redisContainer() {
        return new GenericContainer<>(DockerImageName.parse("redis:latest")).withExposedPorts(6379);
    }

    @Bean
    public GenericContainer<?> minioContainer() {
        return new GenericContainer<>(DockerImageName.parse("minio/minio:latest"))
                .withExposedPorts(9000)
                .withEnv("MINIO_ROOT_USER", MINIO_USER)
                .withEnv("MINIO_ROOT_PASSWORD", MINIO_PASS)
                .withCommand("server", "/data");
    }

    @Bean
    @Primary
    public MinioClient testMinioClient(GenericContainer<?> minioContainer) {

        minioContainer.start();

        String endpoint = String.format("http://%s:%d",
                minioContainer.getHost(),
                minioContainer.getMappedPort(9000));

        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(MINIO_USER, MINIO_PASS)
                .build();
    }

    @DynamicPropertySource
    static void containersProperties(DynamicPropertyRegistry registry,
                                     GenericContainer<?> minioContainer) {

        if (!minioContainer.isRunning()) {
            minioContainer.start();
        }

        registry.add("MINIO_ENDPOINT", () ->
                String.format("http://%s:%d",
                        minioContainer.getHost(),
                        minioContainer.getMappedPort(9000)));
        registry.add("MINIO_ADMIN", () -> MINIO_USER);
        registry.add("MINIO_PASS", () -> MINIO_PASS);
    }

}

