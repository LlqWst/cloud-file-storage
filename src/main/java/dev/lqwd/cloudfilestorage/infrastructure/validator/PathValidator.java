package dev.lqwd.cloudfilestorage.infrastructure.validator;

import dev.lqwd.cloudfilestorage.exception.BadRequestException;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;


@Component
@NoArgsConstructor
public class PathValidator {

    private static final Set<Character> FORBIDDEN_CHARS = Set.of('*', ':', '<', '>', '\\', '|', '?');
    private static final String SLASH = "/";
    public static final int MAX_NAME_LENGTH = 200;

    public void validateDirPath(String path) {
        validatePath(path);
        validateOnEndSlash(path);
    }

    public void validateFilePath(String path) {
        validatePath(path);
        validateOnAbsenceEndSlash(path);
    }

    public void validatePath(String path) {
        validateOnNull(path);
        validateOnForbiddenChars(path);
        validateOnBeginningSlash(path);
        validateNameLength(path);
    }

    private static void validateOnEndSlash(String path) {
        if (!path.endsWith(SLASH) && !path.isEmpty()) {
            throw new BadRequestException("Resource is not a directory: directory should end with '/'");
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

    private static void validateOnForbiddenChars(String path) {
        for (char c : path.toCharArray()) {
            if (FORBIDDEN_CHARS.contains(c)) {
                throw new BadRequestException(
                        "Please enter a resource name that doesn't include any of these chars: " + FORBIDDEN_CHARS);
            }
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
        if (path.length() >= MAX_NAME_LENGTH) {
            throw new BadRequestException(
                    "The resource name '%s' exceeded max allowed name length %d.".formatted(path, MAX_NAME_LENGTH));
        }
    }

}
