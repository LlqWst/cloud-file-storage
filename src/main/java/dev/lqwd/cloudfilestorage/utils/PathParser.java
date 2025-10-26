package dev.lqwd.cloudfilestorage.utils;

import dev.lqwd.cloudfilestorage.model.ProcessedPath;
import org.springframework.stereotype.Component;


@Component
public class PathParser {

    public static final int BEGIN_INDEX = 0;
    public static final String SLASH = "/";
    public static final String DELETE = "";
    private static final String DEFAULT_FOLDER = "Folder";

    public ProcessedPath pars(String normalizedPath) {
        return ProcessedPath.builder()
                .fullPath(getFullPath(normalizedPath))
                .name(getDirName(normalizedPath))
                .parent(getDirParent(normalizedPath))
                .build();
    }

    public String getFullPath(String normalizedPath) {
        return normalizedPath;
    }

    public String getDirName(String normalizedPath) {
        int lastSlash = normalizedPath.lastIndexOf(SLASH);
        int dirCount = normalizedPath.split(SLASH).length;

        if (dirCount == 1){
            return normalizedPath.replace(SLASH, DELETE);
        }
        return lastSlash > BEGIN_INDEX ? normalizedPath.substring(lastSlash + 1) : DEFAULT_FOLDER;
    }

    public String getDirParent(String normalizedPath) {
        int lastSlash = normalizedPath.lastIndexOf(SLASH);
        int dirCount = normalizedPath.split(SLASH).length;

        if (dirCount == 1){
            return SLASH;
        }
        return lastSlash > BEGIN_INDEX ? normalizedPath.substring(lastSlash + 1) : normalizedPath;
    }

}

