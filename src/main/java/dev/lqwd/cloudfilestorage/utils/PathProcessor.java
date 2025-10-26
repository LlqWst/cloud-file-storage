package dev.lqwd.cloudfilestorage.utils;

import dev.lqwd.cloudfilestorage.model.ProcessedPath;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PathProcessor {
    private final Validator validator;
    private final PathNormalizer pathNormalizer;
    private final PathParser pathParser;

    public ProcessedPath process(String rawPath) {
        validator.validatePath(rawPath);
        String normalized = pathNormalizer.normalizePath(rawPath);
        return pathParser.pars(normalized);
    }

}

