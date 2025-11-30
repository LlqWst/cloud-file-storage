package dev.lqwd.cloudfilestorage.utils;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.nio.file.Path;


@Slf4j
@Component
@NoArgsConstructor
public class PathNormalizer {

    private static final String SLASH = "/";
    private static final int BEGINNING = 0;
    private static final int END_SLASH = 1;

    public String normalize(String path) {
        if (path == null || path.isBlank()){
            return SLASH;
        }

        return path.trim()
                .replace("\\", SLASH)
                .replaceAll("/{2,}", SLASH);
    }

    public String normalize(Path path){
        return path.toString()
                .replace("\\", SLASH);
    }

    @NotNull
    public String getPathWithoutEndSlash(String path) {
        String pathWithoutSlash;
        if (path.endsWith(SLASH)) {
            pathWithoutSlash = path.substring(BEGINNING, path.length() - END_SLASH);
        } else {
            pathWithoutSlash = path;
        }
        return pathWithoutSlash;
    }

}
