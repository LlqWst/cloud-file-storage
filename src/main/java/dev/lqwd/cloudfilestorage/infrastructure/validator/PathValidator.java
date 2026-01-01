package dev.lqwd.cloudfilestorage.infrastructure.validator;

import dev.lqwd.cloudfilestorage.exception.BadRequestException;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;


@Component
@NoArgsConstructor
public class PathValidator {

    private static final Set<Character> FORBIDDEN_CHARS = Set.of('*', ':', '<', '>', '\\', '|', '?');
    private static final String SLASH = "/";

    public void validatePath(String path) {
        if (path == null) {
            throw new BadRequestException("Path cannot be null");
        }

        for (char c : path.toCharArray()) {
            if (FORBIDDEN_CHARS.contains(c)) {
                throw new BadRequestException("Please enter a resource name that doesn't include any of these characters: " + FORBIDDEN_CHARS);
            }
        }

        if (path.startsWith(SLASH) && path.length() > 1) {
            throw new BadRequestException("Path cannot start with '/'");
        }
    }

    public void validateDirPath(String path) {
        validatePath(path);

        if (!path.endsWith(SLASH) && !path.isEmpty()) {
            throw new BadRequestException("Resource is not a directory: directory should end with '/'");
        }
    }

    public void validateFilePath(String path) {
        validatePath(path);

        if (path.endsWith(SLASH) || path.isEmpty()) {
            throw new BadRequestException("Resource is not a file: file should not end with '/' and can't be empty");
        }
    }

}
