package dev.lqwd.cloudfilestorage.service.storage.operations;

import dev.lqwd.cloudfilestorage.TestcontainersConfiguration;
import dev.lqwd.cloudfilestorage.repository.storage.minio.MinioBucketStorage;
import dev.lqwd.cloudfilestorage.service.storage.provider.minio.ModificationsMinioService;
import dev.lqwd.cloudfilestorage.service.storage.provider.minio.ValidationMinioService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Set;


@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
abstract class BaseServiceTest {

    protected static final long TEST_ID = 1;
    protected static final String TEST_BUCKET = "test-bucket";
    protected static final String ROOT = "";
    protected static final String EMPTY = "";
    protected static final String SLASH = "/";
    protected static final Set<Character> FORBIDDEN_CHARS = Set.of('*', ':', '<', '>', '\\', '|', '?');
    protected static final String FILE_CONTENT_TYPE = "text/plain";
    protected static final String MULTIPART_HTTP_PARAMETER = "object";

    protected static final String TEST_CORRECT_FILE = "file.txt";
    protected static final String TEST_CORRECT_FOLDER = "folder_test/";
    protected static final String TEST_PARENT_FOLDER = "parent_path/";
    protected static final String TEST_FOLDER_WITHOUT_END_SLASH = "folder_test";
    protected static final String TEST_FOLDER_WITH_PARENT = TEST_PARENT_FOLDER + TEST_CORRECT_FOLDER;

    @Autowired
    protected MinioBucketStorage bucketProviderStorage;

    @Autowired
    protected CreationService creationService;

    @Autowired
    protected ValidationMinioService validationProviderService;

    @Autowired
    protected ModificationsMinioService modificationsProviderService;

    @Autowired
    protected FindService findService;

    @Autowired
    protected UploadService uploadService;

    @BeforeAll
    static void setUpTestBucket() {
        System.setProperty("app.bucket.name", TEST_BUCKET);
    }

    @BeforeEach
    void setUp() {
        bucketProviderStorage.createBucketIfNotExists();
        creationService.createUserRootDir(TEST_ID);
        modificationsProviderService.removeDir(ROOT, TEST_ID);
    }

    protected static @NotNull MockMultipartFile createFile(String fileName, String content) {
        return new MockMultipartFile(
                MULTIPART_HTTP_PARAMETER,
                fileName,
                FILE_CONTENT_TYPE,
                content.getBytes()
        );
    }

    protected void uploadTestFileInRootDir() {
        String content = "Test file content";
        MultipartFile[] file = new MockMultipartFile[]{
                createFile(TEST_CORRECT_FILE, content)
        };
        uploadService.upload(ROOT, TEST_ID, file);
    }

    protected void createTestFolderInRootDir(){
        creationService.createDir(TEST_CORRECT_FOLDER, TEST_ID);
    }

}