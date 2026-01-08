package dev.lqwd.cloudfilestorage.infrastructure.path;

import dev.lqwd.cloudfilestorage.entity.Type;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import static dev.lqwd.cloudfilestorage.util.PathConstant.SLASH;
import static dev.lqwd.cloudfilestorage.util.PathConstant.EMPTY;

import java.nio.file.Path;
import java.util.Optional;


@Component
@AllArgsConstructor
public class PathParsHelper {

    private final PathNormalizer pathNormalizer;

    public String getName(Path path) {
        Optional<Path> name = Optional.ofNullable(
                path.getFileName());

        if (name.isPresent()) {
            return pathNormalizer.replaceBackSlashToSlash(name.get());
        }
        return EMPTY;
    }

    public String getParentPath(Path path) {
        Path parentPath = path.getParent();
        if (parentPath == null) {
            return EMPTY;
        }
        return pathNormalizer.replaceBackSlashToSlash(parentPath) + SLASH;
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

