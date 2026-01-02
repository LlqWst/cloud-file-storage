package dev.lqwd.cloudfilestorage.service.storage.operations;

import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDto;
import dev.lqwd.cloudfilestorage.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


class FindServiceTest extends BaseServiceTest {

    @Test
    void ShouldThrowException_When_FileDoesntExists() {
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                findService.getResource(TEST_CORRECT_FILE, TEST_ID));

        assertEquals("Resource doesn't exists: " + TEST_CORRECT_FILE, exception.getMessage());
    }

    @Test
    void ShouldReturnResource_When_ResourceExists() {
        createTestFolderInRootDir();

        ResourceResponseDto answer = findService.getResource(TEST_CORRECT_FOLDER, TEST_ID);
        assertNotNull(answer);
        assertEquals(TEST_FOLDER_WITHOUT_END_SLASH, answer.name());
    }

    @Test
    void ShouldReturnResources_From_RootDir() {
        createTestFolderInRootDir();
        uploadTestFileInRootDir();

        Set<String> expectedNames = Set.of(
                TEST_FOLDER_WITHOUT_END_SLASH,
                TEST_CORRECT_FILE
        );

        List<ResourceResponseDto> answer = findService.getResources(ROOT, TEST_ID);
        assertNotNull(answer);
        assertEquals(expectedNames.size(), answer.size());

        Set<String> actualNames = answer.stream()
                .map(ResourceResponseDto::name)
                .collect(Collectors.toSet());

        assertEquals(expectedNames, actualNames);
    }

    @Test
    void ShouldReturnAllMatchedResources_When_QueryContains() {
        createTestFolderInRootDir();
        uploadTestFileInRootDir();

        String newFolder = TEST_CORRECT_FOLDER + "new_folder/";
        creationService.createDir(newFolder, TEST_ID);

        String newFolderName2 = "new_fi_folder";
        String newFolder_2 = TEST_CORRECT_FOLDER + newFolderName2 + SLASH;
        creationService.createDir(newFolder_2, TEST_ID);

        String content = "test 123";
        String newFileName = "fi.txt";
        MultipartFile[] file = new MockMultipartFile[] {
                createFile(newFileName, content)
        };
        uploadService.upload(newFolder, TEST_ID, file);

        Set<String> expectedNames = Set.of(
                newFileName,
                TEST_CORRECT_FILE,
                newFolderName2
        );

        String query = "fi";
        List<ResourceResponseDto> answer = findService.searchResource(query, TEST_ID);
        assertNotNull(answer);
        assertEquals(expectedNames.size(), answer.size());

        Set<String> actualNames = answer.stream()
                .map(ResourceResponseDto::name)
                .collect(Collectors.toSet());

        assertEquals(expectedNames, actualNames);
    }

}