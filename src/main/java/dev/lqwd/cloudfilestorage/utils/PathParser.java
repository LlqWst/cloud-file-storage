package dev.lqwd.cloudfilestorage.utils;

import dev.lqwd.cloudfilestorage.model.ProcessedPath;
import dev.lqwd.cloudfilestorage.model.Type;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;


@Component
@AllArgsConstructor
public class PathParser {

    public static final String SLASH = "/";
    public static final String EMPTY = "";
    private final PathNormalizer pathNormalizer;

    public ProcessedPath pars(String normalizedPath) {
        Path path = Paths.get(normalizedPath);

        return ProcessedPath.builder()
                .requestedPath(normalizedPath)
                .name(getName(path))
                .path(getParentPath(path))
                .type(getType(normalizedPath))
                .build();
    }

    private String getName(Path path) {
        Path dirName = path.getFileName();
        return pathNormalizer.normalize(dirName);
    }

    private String getParentPath(Path path) {
        Path parentPath = path.getParent();
        if (parentPath == null) {
            return EMPTY;
        }
        return pathNormalizer.normalize(parentPath) + SLASH;
    }

    private Type getType(String resourceName) {
        return resourceName.endsWith(SLASH) ? Type.DIRECTORY : Type.FILE;
    }

}

