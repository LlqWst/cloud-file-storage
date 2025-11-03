package dev.lqwd.cloudfilestorage.utils;

import dev.lqwd.cloudfilestorage.exception.BadRequestException;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@NoArgsConstructor
public class PathValidator {

    public void validatePath(String path) {
        if (path == null || path.trim().isBlank()) {
            throw new BadRequestException("Path is empty");
        }

        if (path.contains("*") ||
            path.contains(":") ||
            path.contains("<") ||
            path.contains(">") ||
            path.contains("\\") ||
            path.contains("|") ||
            path.contains("?") ||
            (path.startsWith("/") && path.length() > 1)
        ) {
            throw new BadRequestException("Please enter a resourceName that doesn't include any of these characters: .*:<>?/\\|");
        }
    }

    public void validateDirPath(String path) {
        validatePath(path);

        if (!path.endsWith("/") || path.contains(".")) {
            throw new BadRequestException("Resource is not a directory: directory should end with '/'");
        }
    }

    public void validateFilePath(String path) {
        validatePath(path);

        if (path.endsWith("/")) {
            throw new BadRequestException("Resource is not a file: file should not end with '/'");
        }
    }
}
