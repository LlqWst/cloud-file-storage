package dev.lqwd.cloudfilestorage.utils.path_processor;

import dev.lqwd.cloudfilestorage.utils.PathNormalizer;
import dev.lqwd.cloudfilestorage.utils.PathValidator;
import dev.lqwd.cloudfilestorage.utils.parser.PathParser;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PathProcessor {
    private final PathNormalizer normalizer;
    private final PathParser parser;
    private final PathValidator validator;

    public ProcessedPath processResource(String rawPath) {
        String normalized = normalize(rawPath);
        validator.validatePath(rawPath);
        return pars(normalized);
    }

    public ProcessedPath processDir(String rawPath) {
        String normalized = normalize(rawPath);
        validator.validateDirPath(rawPath);
        return pars(normalized);
    }

    public ProcessedPath processFile(String rawPath) {
        String normalized = normalize(rawPath);
        validator.validateFilePath(rawPath);
        return pars(normalized);
    }

    private String normalize(String rawPath) {
        return normalizer.normalize(rawPath);
    }

    private ProcessedPath pars(String normalized) {
        return parser.pars(normalized);
    }

}

