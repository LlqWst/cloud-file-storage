package dev.lqwd.cloudfilestorage.infrastructure.path;

import dev.lqwd.cloudfilestorage.dto.property.ValidationProperties;
import dev.lqwd.cloudfilestorage.exception.BadRequestException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static dev.lqwd.cloudfilestorage.util.PathConstant.SLASH;
import static dev.lqwd.cloudfilestorage.util.RepeatableErrorMessage.*;


@Component
@AllArgsConstructor
public class PathValidator {

    private final ValidationProperties properties;

    public void validatePath(String path) {
        validateOnNull(path);
        validateOnForbiddenChars(path);
        validateOnBeginningSlash(path);
        validateNameLength(path);
    }

    public void validateDirPath(String path) {
        validatePath(path);
        validateOnEndSlash(path);
    }

    public void validateFilePath(String path) {
        validatePath(path);
        validateOnAbsenceEndSlash(path);
    }

    private static void validateOnEndSlash(String path) {
        if (!path.endsWith(SLASH) && !path.isEmpty()) {
            throw new BadRequestException(NOT_DIRECTORY_ERROR_MESSAGE);
        }
    }

    private static void validateOnAbsenceEndSlash(String path) {
        if (path.endsWith(SLASH) || path.isEmpty()) {
            throw new BadRequestException("Resource is not a file: file should not end with '/' and can't be empty");
        }
    }

    private static void validateOnBeginningSlash(String path) {
        if (path.startsWith(SLASH) && path.length() > 1) {
            throw new BadRequestException("Path cannot start with '/'");
        }
    }

    private void validateOnForbiddenChars(String path) {
        boolean hasForbidden = path.chars()
                .anyMatch(c -> properties.forbiddenChars().contains((char) c));
        if (hasForbidden) {
            throw new BadRequestException(
                    RESOURCE_INCORRECT_NAME_ERROR_MESSAGE + properties.forbiddenChars());
        }
    }

private static void validateOnNull(String path) {
    if (path == null) {
        throw new BadRequestException("Path cannot be null");
    }
}

private void validateNameLength(String path) {
    Arrays.stream(path.split(SLASH))
            .forEach(this::validateOnMaxLength);
}

private void validateOnMaxLength(String path) {
    if (path.length() >= properties.maxLengthPathName()) {
        throw new BadRequestException(
                RESOURCE_EXCEEDED_LENGTH_NAME_ERROR_MESSAGE.formatted(path, properties.maxLengthPathName()));
    }
}

}
