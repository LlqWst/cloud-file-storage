package dev.lqwd.cloudfilestorage.service.storage.operations;

import dev.lqwd.cloudfilestorage.exception.AlreadyExistException;
import dev.lqwd.cloudfilestorage.exception.BadRequestException;
import dev.lqwd.cloudfilestorage.exception.NotFoundException;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static dev.lqwd.cloudfilestorage.util.PathConstant.EMPTY;
import static dev.lqwd.cloudfilestorage.util.PathConstant.SLASH;
import static dev.lqwd.cloudfilestorage.util.RepeatableErrorMessage.*;
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
    void ShouldThrowBadRequestException_When_TooLargeName() {
        Exception exception = assertThrows(BadRequestException.class, () ->
                creationService.createDir(getNameMoreThanMaxNameLength(), TEST_ID));

        assertEquals(RESOURCE_EXCEEDED_LENGTH_NAME_ERROR_MESSAGE
                        .formatted(getNameMoreThanMaxNameLength(), properties.maxLengthPathName()),
                exception.getMessage());
    }

    @Test
    void ShouldThrowBadRequestException_When_CreateFolder_Without_EndSlash() {
        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                creationService.createDir(TEST_FOLDER_WITHOUT_END_SLASH, TEST_ID));

        assertEquals(NOT_DIRECTORY_ERROR_MESSAGE,
                exception.getMessage());
    }

    @Test
    void ShouldThrowAlreadyExistException_When_CreateFolderDuplicatedFolder() {
        creationService.createDir(TEST_CORRECT_FOLDER, TEST_ID);
        AlreadyExistException exception = assertThrows(AlreadyExistException.class, () ->
                creationService.createDir(TEST_CORRECT_FOLDER, TEST_ID));

        assertEquals(RESOURCE_ALREADY_EXISTS_ERROR_MESSAGE + TEST_CORRECT_FOLDER,
                exception.getMessage());
    }

    @Test
    void ShouldThrowNotFoundException_When_CreateFolder_With_ParentPathDoesntExists() {
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                creationService.createDir(TEST_FOLDER_WITH_PARENT, TEST_ID));

        assertEquals(PARENT_PATH_NOT_EXISTS_ERROR_MESSAGE + TEST_PARENT_FOLDER, exception.getMessage());
    }

    @Test
    void ShouldThrowBadRequestException_When_CreateFolder_With_NameHasOneOfForbiddenChar() {
        properties.forbiddenChars().forEach(symbol -> {
                    Exception exception = assertThrows(BadRequestException.class, () ->
                            creationService.createDir(TEST_FOLDER_WITHOUT_END_SLASH + symbol + SLASH, TEST_ID)
                    );
                    assertEquals(RESOURCE_INCORRECT_NAME_ERROR_MESSAGE
                            + properties.forbiddenChars(),
                            exception.getMessage());
                }
        );
    }

    @Test
    void ShouldThrowBadRequestException_When_CreateFolder_With_NameIsRoot() {
        Set<String> TEST_PARAMETERS = Set.of(EMPTY, SLASH);

        TEST_PARAMETERS.forEach(parameter -> {
                    Exception exception = assertThrows(BadRequestException.class, () ->
                            creationService.createDir(parameter, TEST_ID)
                    );
                    assertEquals(RESOURCE_NAME_IS_EMPTY_ERROR_MESSAGE,
                            exception.getMessage());
                }
        );
    }

    @Test
    void ShouldThrowBadRequestException_When_CreateFolder_With_NameIsNull() {
        Exception exception = assertThrows(BadRequestException.class, () ->
                creationService.createDir(null, TEST_ID));

        assertEquals(RESOURCE_NAME_IS_EMPTY_ERROR_MESSAGE,
                exception.getMessage());
    }

    @Test
    void ShouldThrowAlreadyExistException_When_CreateFolder_When_FileExistsWithTheSameName() {
        uploadTestFileInRootDir();
        AlreadyExistException exception = assertThrows(AlreadyExistException.class, () ->
                creationService.createDir(TEST_CORRECT_FILE + SLASH, TEST_ID));

        assertEquals(RESOURCE_ALREADY_EXISTS_ERROR_MESSAGE + TEST_CORRECT_FILE + SLASH,
                exception.getMessage());
    }

}