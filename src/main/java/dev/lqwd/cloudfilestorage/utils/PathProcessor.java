package dev.lqwd.cloudfilestorage.utils;

import dev.lqwd.cloudfilestorage.model.ProcessedPath;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PathProcessor {
    private final PathNormalizer pathNormalizer;
    private final PathParser pathParser;

    public ProcessedPath process(String rawPath) {
        String normalized = pathNormalizer.normalize(rawPath);
        return pathParser.pars(normalized);
    }

}

