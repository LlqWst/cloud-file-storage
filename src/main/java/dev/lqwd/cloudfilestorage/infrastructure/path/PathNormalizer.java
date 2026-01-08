package dev.lqwd.cloudfilestorage.infrastructure.path;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import static dev.lqwd.cloudfilestorage.util.PathConstant.SLASH;
import static dev.lqwd.cloudfilestorage.util.PathConstant.EMPTY;
import java.nio.file.Path;


@Component
public class PathNormalizer {

    private static final int FIRST_CHAR = 0;
    private static final int END_SLASH = 1;

    public String normalize(String path) {
        if (path == null || path.isBlank()) {
            return EMPTY;
        }

        String trimmedPath = path.trim()
                .replaceAll("/{2,}", SLASH);

        return trimmedPath.equals(SLASH) ? EMPTY : trimmedPath;
    }

    public String replaceBackSlashToSlash(Path path) {
        return path.toString()
                .replace("\\", SLASH);
    }

    @NotNull
    public String getPathWithoutEndSlash(String path) {
        if (path.endsWith(SLASH)) {
            return path.substring(FIRST_CHAR, path.length() - END_SLASH);
        }
        return path;
    }

}
