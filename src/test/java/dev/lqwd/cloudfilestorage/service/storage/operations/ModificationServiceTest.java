package dev.lqwd.cloudfilestorage.service.storage.operations;

import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDto;
import dev.lqwd.cloudfilestorage.exception.AlreadyExistException;
import dev.lqwd.cloudfilestorage.exception.BadRequestException;
import dev.lqwd.cloudfilestorage.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.stream.Collectors;

import static dev.lqwd.cloudfilestorage.util.RepeatableErrorMessage.*;
import static org.junit.jupiter.api.Assertions.*;
import static dev.lqwd.cloudfilestorage.util.PathConstant.ROOT;
import static dev.lqwd.cloudfilestorage.util.PathConstant.SLASH;


class ModificationServiceTest extends BaseServiceTest {

    @Test
    void ShouldDeleteFolder_When_Exists() {
        createTestFolderInRootDir();

        modificationService.removeResource(TEST_CORRECT_FOLDER, TEST_ID);
        assertFalse(validationProviderService.isExist(TEST_CORRECT_FOLDER, TEST_ID));
    }

    @Test
    void ShouldDeleteFile_When_Exists() {
        uploadTestFileInRootDir();

        modificationService.removeResource(TEST_CORRECT_FILE, TEST_ID);
        assertFalse(validationProviderService.isExist(TEST_CORRECT_FILE, TEST_ID));
    }

    @Test
    void ShouldThrowNotFoundException_When_NotExists() {
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                modificationService.removeResource(TEST_CORRECT_FOLDER, TEST_ID));

        assertEquals(RESOURCE_NOT_EXISTS_ERROR_MESSAGE + TEST_CORRECT_FOLDER, exception.getMessage());

    }

    @Test
    void ShouldRenameFile() {
        uploadTestFileInRootDir();
        String newName = "New_file_name.txt";
        modificationService.moveResource(TEST_CORRECT_FILE, newName, TEST_ID);

        assertFalse(validationProviderService.isExist(TEST_CORRECT_FILE, TEST_ID));
        assertTrue(validationProviderService.isExist(newName, TEST_ID));
    }

    @Test
    void ShouldThrowAlreadyExistsException_When_FileExistsWithSameName() {
        String folderName = "1/";
        String firstFilePath = folderName + "1";
        String secondFilePath = folderName + "2";

        MultipartFile[] files = new MockMultipartFile[]{
                createFile(firstFilePath),
                createFile(secondFilePath),
        };
        uploadService.upload(ROOT, TEST_ID, files);

        AlreadyExistException exception = assertThrows(AlreadyExistException.class, () ->
                modificationService.moveResource(secondFilePath, firstFilePath, TEST_ID));

        assertEquals(RESOURCE_ALREADY_EXISTS_ERROR_MESSAGE + firstFilePath, exception.getMessage());
    }

    @Test
    void ShouldRenameOnlyInnerFile_When_ParentFolderHasSameName() {
        String folderNameWithoutSlash = "1";
        String firstFileName = "2";
        String secondFileName = "3";

        String folderName = folderNameWithoutSlash + SLASH;
        String firstFilePath = folderName + firstFileName;
        String secondFilePath = folderName + secondFileName;
        String newFileName = folderName + folderNameWithoutSlash;

        MultipartFile[] files = new MockMultipartFile[]{
                createFile(firstFilePath),
                createFile(secondFilePath),
        };
        uploadService.upload(ROOT, TEST_ID, files);
        modificationService.moveResource(firstFilePath, newFileName, TEST_ID);

        Set<String> expect1 = Set.of(folderNameWithoutSlash);
        Set<String> answer1 = findService.getResources(ROOT, TEST_ID)
                .stream()
                .map(ResourceResponseDto::name)
                .collect(Collectors.toSet());

        assertEquals(expect1.size(), answer1.size());
        assertEquals(expect1, answer1);

        Set<String> expect2 = Set.of(folderNameWithoutSlash, secondFileName);
        Set<String> answer2 = findService.getResources(folderName, TEST_ID)
                .stream()
                .map(ResourceResponseDto::name)
                .collect(Collectors.toSet());

        assertEquals(expect2.size(), answer2.size());
        assertEquals(expect2, answer2);
    }

    @Test
    void ShouldMoveFile() {
        String folderName1 = "1";
        String folderPath1 = folderName1 + SLASH;

        String folderName2 = "2";
        String folderPath2 = folderName2 + SLASH;

        String fileName1 = "2";
        String filePath1 = folderPath1 + fileName1;
        String fileNewPath = folderPath2 + fileName1;

        MultipartFile[] files = new MockMultipartFile[]{
                createFile(filePath1)
        };

        uploadService.upload(ROOT, TEST_ID, files);
        creationService.createDir(folderPath2, TEST_ID);
        modificationService.moveResource(filePath1, fileNewPath, TEST_ID);

        Set<String> answer1 = findService.getResources(folderPath1, TEST_ID)
                .stream()
                .map(ResourceResponseDto::name)
                .collect(Collectors.toSet());

        assertTrue(answer1.isEmpty());

        Set<String> expect2 = Set.of(fileName1);
        Set<String> answer2 = findService.getResources(folderPath2, TEST_ID)
                .stream()
                .map(ResourceResponseDto::name)
                .collect(Collectors.toSet());

        assertEquals(expect2.size(), answer2.size());
        assertEquals(expect2, answer2);
    }

    @Test
    void ShouldMoveFolderRecursive() {
        String folderName1 = "+";
        String folderPath1 = folderName1 + SLASH;

        String folderName2 = "2";
        String folderPath2 = folderName2 + SLASH;

        String fileName1 = "+";
        String filePath1 = folderPath1 + fileName1;

        String fileName2 = "2";
        String filePath2 = folderPath1 + fileName2;

        MultipartFile[] files = new MockMultipartFile[]{
                createFile(filePath1),
                createFile(filePath2)
        };

        uploadService.upload(ROOT, TEST_ID, files);
        creationService.createDir(folderPath2, TEST_ID);

        String folder1NewPath = folderPath2 + folderPath1;
        modificationService.moveResource(folderPath1, folder1NewPath, TEST_ID);

        Set<String> expect1 = Set.of(folderName2);
        Set<String> answer1 = findService.getResources(ROOT, TEST_ID)
                .stream()
                .map(ResourceResponseDto::name)
                .collect(Collectors.toSet());

        assertEquals(expect1.size(), answer1.size());
        assertEquals(expect1, answer1);

        Set<String> expect2 = Set.of(folderName1);
        Set<String> answer2 = findService.getResources(folderPath2, TEST_ID)
                .stream()
                .map(ResourceResponseDto::name)
                .collect(Collectors.toSet());

        assertEquals(expect2.size(), answer2.size());
        assertEquals(expect2, answer2);

        Set<String> expect3 = Set.of(fileName1, fileName2);
        Set<String> answer3 = findService.getResources(folder1NewPath, TEST_ID)
                .stream()
                .map(ResourceResponseDto::name)
                .collect(Collectors.toSet());

        assertEquals(expect3.size(), answer3.size());
        assertEquals(expect3, answer3);
    }

    @Test
    void ShouldThrowBadRequestException_When_MoveToItself() {
        String folderName1 = "1";
        String folderPath1 = folderName1 + SLASH;
        creationService.createDir(folderPath1, TEST_ID);

        String folderName2 = "2";
        String innerFolder = folderPath1 + folderName2 + SLASH;
        creationService.createDir(innerFolder, TEST_ID);

        String folder1NewPath = folderPath1 + folderPath1;
        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                modificationService.moveResource(folderPath1, folder1NewPath, TEST_ID));


        assertEquals(MOVE_TO_ITSELF_ERROR_MESSAGE, exception.getMessage());
    }

}