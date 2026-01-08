package dev.lqwd.cloudfilestorage.util;

import dev.lqwd.cloudfilestorage.entity.Type;
import dev.lqwd.cloudfilestorage.infrastructure.path.processor.ProcessedPath;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class PathTypeUtils {

    public static boolean isDirectory(ProcessedPath path) {
        return path.type().equals(Type.DIRECTORY);
    }

}
