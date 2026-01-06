package dev.lqwd.cloudfilestorage.infrastructure.validator;

import dev.lqwd.cloudfilestorage.exception.BadRequestException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;


@Component
public class PathValidator {

    @Value("${app.path.forbidden.chars}")
    private Character[] forbiddenCharacters;

    private Set<Character> forbiddenSet;
    private static final String SLASH = "/";

    @Value("${app.max.length.name}")
    private int maxLengthName;

    @PostConstruct
    public void init() {
        forbiddenSet = Set.of(forbiddenCharacters);
    }

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

    private void validateOnForbiddenChars(String path) {
        for (char c : path.toCharArray()) {
            if (forbiddenSet.contains(c)) {
                throw new BadRequestException(
                        "Please enter a resource name that doesn't include any of these chars: " + forbiddenSet);
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
        if (path.length() >= maxLengthName) {
            throw new BadRequestException(
                    "The resource name '%s' exceeded max allowed name length %d.".formatted(path, maxLengthName));
        }
    }

}
