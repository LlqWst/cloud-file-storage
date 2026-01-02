package dev.lqwd.cloudfilestorage.service.storage.operations;

import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;


class UploadServiceTest extends BaseServiceTest {

    @Test
    void ShouldUploadFile() {

        String content = "Test file content";
        MultipartFile[] file = new MockMultipartFile[]{
                createFile(TEST_CORRECT_FILE,  content)
        };
        uploadService.upload(ROOT, TEST_ID, file);

        ResourceResponseDto answer = findService.getResource(TEST_CORRECT_FILE, TEST_ID);
        assertNotNull(answer);
        assertEquals(TEST_CORRECT_FILE, answer.name());
    }

}