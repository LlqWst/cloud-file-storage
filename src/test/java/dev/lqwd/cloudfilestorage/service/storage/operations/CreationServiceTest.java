package dev.lqwd.cloudfilestorage.service.storage.operations;

import dev.lqwd.cloudfilestorage.exception.AlreadyExistException;
import dev.lqwd.cloudfilestorage.exception.BadRequestException;
import dev.lqwd.cloudfilestorage.exception.NotFoundException;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


class CreationServiceTest extends BaseServiceTest {

    @Test
    void ShouldCreateFolder() {
        assertDoesNotThrow(() ->
                validationProviderService.validateOnExistence(TEST_CORRECT_FOLDER, TEST_ID));

        creationService.createDir(TEST_CORRECT_FOLDER, TEST_ID);
        assertDoesNotThrow(() ->
                validationProviderService.validateOnAbsence(TEST_CORRECT_FOLDER, TEST_ID));
    }

    @Test
    void ShouldThrowException_When_CreateFolder_Without_EndSlash() {
        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                creationService.createDir(TEST_FOLDER_WITHOUT_END_SLASH, TEST_ID));

        assertEquals("Resource is not a directory: directory should end with '/'",
                exception.getMessage());
    }

    @Test
    void ShouldThrowException_When_CreateFolderDuplicatedFolder() {
        creationService.createDir(TEST_CORRECT_FOLDER, TEST_ID);
        AlreadyExistException exception = assertThrows(AlreadyExistException.class, () ->
                creationService.createDir(TEST_CORRECT_FOLDER, TEST_ID));

        assertEquals("Resource already exists: " + TEST_CORRECT_FOLDER,
                exception.getMessage());
    }

    @Test
    void ShouldThrowException_When_CreateFolder_With_ParentPathDoesntExists() {
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                creationService.createDir(TEST_FOLDER_WITH_PARENT, TEST_ID));

        assertEquals("Parent path doesn't exist: " + TEST_PARENT_FOLDER, exception.getMessage());
    }

    @Test
    void ShouldThrowException_When_CreateFolder_With_NameHasOneOfForbiddenChar() {
        FORBIDDEN_CHARS.forEach(symbol -> {
                    Exception exception = assertThrows(BadRequestException.class, () ->
                            creationService.createDir(TEST_FOLDER_WITHOUT_END_SLASH + symbol + SLASH, TEST_ID)
                    );
                    assertEquals(
                            "Please enter a resource name that doesn't include any of these characters: "
                            + FORBIDDEN_CHARS,
                            exception.getMessage());
                }
        );
    }

    @Test
    void ShouldThrowException_When_CreateFolder_With_NameIsRoot() {
        Set<String> TEST_PARAMETERS = Set.of(EMPTY, SLASH);

        TEST_PARAMETERS.forEach(parameter -> {
                    Exception exception = assertThrows(BadRequestException.class, () ->
                            creationService.createDir(parameter, TEST_ID)
                    );
                    assertEquals("Resource name is empty or equals '/'",
                            exception.getMessage());
                }
        );
    }

    @Test
    void ShouldThrowException_When_CreateFolder_With_NameIsNull() {
        Exception exception = assertThrows(BadRequestException.class, () ->
                creationService.createDir(null, TEST_ID));

        assertEquals(
                "Resource name is empty or equals '/'",
                exception.getMessage());
    }

    @Test
    void ShouldThrowException_When_CreateFolder_When_FileExistsWithTheSameName() {
        uploadTestFileInRootDir();
        AlreadyExistException exception = assertThrows(AlreadyExistException.class, () ->
                creationService.createDir(TEST_CORRECT_FILE + SLASH, TEST_ID));

        assertEquals("Resource already exists: " + TEST_CORRECT_FILE + SLASH,
                exception.getMessage());
    }

}