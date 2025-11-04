package dev.lqwd.cloudfilestorage.utils.parser;

import dev.lqwd.cloudfilestorage.entity.Type;
import dev.lqwd.cloudfilestorage.utils.PathNormalizer;
import lombok.AllArgsConstructor;

import java.nio.file.Path;
import java.util.Optional;

@AllArgsConstructor
public class AbstractParser {

    protected static final String SLASH = "/";
    protected static final String EMPTY = "";
    private final PathNormalizer pathNormalizer;

    protected String getName(Path path) {
        Optional<Path> name = Optional.ofNullable(
                path.getFileName());
        if (name.isPresent()) {
            return pathNormalizer.normalize(name.get());
        }
        return EMPTY;
    }

    protected String getParentPath(Path path) {
        Path parentPath = path.getParent();
        if (parentPath == null) {
            return EMPTY;
        }
        return pathNormalizer.normalize(parentPath) + SLASH;
    }

    protected Type getType(String resourcePath) {
        return resourcePath.endsWith(SLASH) ? Type.DIRECTORY : Type.FILE;
    }

    protected String normalizeRootPath(String dirPath) {
        return dirPath.equals(SLASH) ? EMPTY : dirPath;
    }

    protected String removeUserDir(String fullPath){
        return fullPath.substring(fullPath.indexOf(SLASH)  + 1);
    }

}

