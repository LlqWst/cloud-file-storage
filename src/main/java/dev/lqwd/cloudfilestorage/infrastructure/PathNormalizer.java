package dev.lqwd.cloudfilestorage.infrastructure;

import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.nio.file.Path;


@Component
@NoArgsConstructor
public class PathNormalizer {

    private static final String SLASH = "/";
    private static final String EMPTY = "";
    private static final int FIRST_CHAR = 0;
    private static final int END_SLASH = 1;

    public String normalize(String path) {
        if (path == null ||
            path.isBlank()) {

            return EMPTY;
        }

        String trimmedPath = path.trim()
                .replaceAll("/{2,}", SLASH);

        return trimmedPath.equals(SLASH) ? EMPTY : trimmedPath;
    }

    public String normalize(Path path) {
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
