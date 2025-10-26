package dev.lqwd.cloudfilestorage.utils;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class PathNormalizer {

    public String normalizePath(String path) {
        return path
                .trim()
                .replace("\\", "/");
    }
}
