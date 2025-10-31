package dev.lqwd.cloudfilestorage.utils;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Path;


@Component
@NoArgsConstructor
public class PathNormalizer {

    public String normalize(String path) {
        return path.trim()
                .replace("\\", "/")
                .replaceAll("/{2,}", "/");
    }

    public String normalize(Path path){
        return path.toString()
                .replace("\\", "/");
    }

}
