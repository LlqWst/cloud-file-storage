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
        String normalized = normalizer.normalize(rawPath);
        validator.validatePath(normalized);
        return pars(normalized);
    }

    public ProcessedPath processDir(String rawPath) {
        String normalized = normalizer.normalize(rawPath);
        validator.validateDirPath(normalized);
        return pars(normalized);
    }

    public ProcessedPath processFile(String rawPath) {
        String normalized = normalizer.normalize(rawPath);
        validator.validateFilePath(normalized);
        return pars(normalized);
    }

    private ProcessedPath pars(String normalized) {
        return parser.pars(normalized);
    }

}

