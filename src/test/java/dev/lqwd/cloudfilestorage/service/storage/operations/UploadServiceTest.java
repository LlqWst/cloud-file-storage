package dev.lqwd.cloudfilestorage.service.storage.operations;

import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDto;
import dev.lqwd.cloudfilestorage.exception.AlreadyExistException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class UploadServiceTest extends BaseServiceTest {

    @Test
    void ShouldUploadFile() {
        MultipartFile[] file = new MockMultipartFile[]{
                createFile(TEST_CORRECT_FILE)
        };
        uploadService.upload(ROOT, TEST_ID, file);

        ResourceResponseDto answer = findService.getResource(TEST_CORRECT_FILE, TEST_ID);
        assertNotNull(answer);
        assertEquals(TEST_CORRECT_FILE, answer.name());
    }

    @Test
    void ShouldUploadArrayOfFilesInRootDir() {
        MultipartFile[] files = new MockMultipartFile[]{
                createFile(TEST_CORRECT_FILE),
                createFile(TEST_CORRECT_FILE + "1"),
                createFile(TEST_CORRECT_FILE + "2")
        };
        uploadService.upload(ROOT, TEST_ID, files);

        List<ResourceResponseDto> answer = findService.getResources(ROOT, TEST_ID);
        assertNotNull(answer);
        assertEquals(Arrays.stream(files).count(), answer.size());
    }

    @Test
    void ShouldCreateFolders_If_NameContainsFolders() {
        String folderName = TEST_PARENT_FOLDER;
        String innerFolderName = folderName + TEST_CORRECT_FOLDER;

        MultipartFile[] files = new MockMultipartFile[]{
                createFile(folderName + TEST_CORRECT_FILE),
                createFile(folderName + TEST_CORRECT_FILE + "1"),
                createFile(innerFolderName + TEST_CORRECT_FILE + "2"),
        };
        uploadService.upload(ROOT, TEST_ID, files);

        List<ResourceResponseDto> answerFolder = findService.getResources(folderName, TEST_ID);
        List<ResourceResponseDto> answerInnerFolder = findService.getResources(innerFolderName, TEST_ID);

        assertNotNull(answerFolder);
        assertNotNull(answerInnerFolder);

        List<String> expect = List.of(innerFolderName);
        assertEquals(Arrays.stream(files).count(), answerFolder.size());
        assertEquals(expect.size(), answerInnerFolder.size());
    }

    @Test
    void NoOneFilesShouldBeUploaded_When_OneOfFilesExists() {
        String uploadedFile = TEST_CORRECT_FILE + "1";

        MultipartFile[] file = new MockMultipartFile[]{
                createFile(uploadedFile)
        };
        uploadService.upload(ROOT, TEST_ID, file);

        MultipartFile[] files = new MockMultipartFile[]{
                createFile(TEST_CORRECT_FILE),
                createFile(uploadedFile),
                createFile(TEST_CORRECT_FILE + "2")
        };
        AlreadyExistException exception = assertThrows(AlreadyExistException.class, () ->
                uploadService.upload(ROOT, TEST_ID, files));

        assertEquals("Resource already exists: " + uploadedFile, exception.getMessage());

        List<ResourceResponseDto> answer = findService.getResources(ROOT, TEST_ID);
        assertNotNull(answer);
        assertEquals(Arrays.stream(file).count(), answer.size());
    }

}