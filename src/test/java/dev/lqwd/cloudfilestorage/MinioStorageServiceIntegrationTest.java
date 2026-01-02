package dev.lqwd.cloudfilestorage;

import dev.lqwd.cloudfilestorage.repository.storage.minio.MinioBucketStorage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
public class MinioStorageServiceIntegrationTest {

    @Autowired
    private MinioBucketStorage minioBucketStorage;

    @BeforeAll
    static void setAllUp() {
        System.setProperty("app.bucket.name", "test-bucket-" + System.currentTimeMillis());
    }

    @BeforeEach
    void setUp() {
        minioBucketStorage.createBucketIfNotExists();

    }

    @Test
    void test1() {
        boolean is = minioBucketStorage.isBucketExists();
        assertTrue(is);
    }

}