package dev.lqwd.cloudfilestorage.parser;

import dev.lqwd.cloudfilestorage.entity.Type;
import dev.lqwd.cloudfilestorage.infrastructure.PathNormalizer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Optional;


@Component
@AllArgsConstructor
public class PathParsHelper {

    private static final String SLASH = "/";
    private static final String EMPTY = "";
    private final PathNormalizer pathNormalizer;


    public String getName(Path path) {
        Optional<Path> name = Optional.ofNullable(
                path.getFileName());

        if (name.isPresent()) {
            return pathNormalizer.normalize(name.get());
        }
        return EMPTY;
    }

    public String getParentPath(Path path) {
        Path parentPath = path.getParent();
        if (parentPath == null) {
            return EMPTY;
        }
        return pathNormalizer.normalize(parentPath) + SLASH;
    }

    public Type getType(String resourcePath) {
        if (resourcePath.endsWith(SLASH) || resourcePath.isEmpty()) {
            return Type.DIRECTORY;
        }
        return Type.FILE;
    }

    public String removeUserDir(String fullPath) {
        return fullPath.substring(fullPath.indexOf(SLASH) + 1);
    }

}

