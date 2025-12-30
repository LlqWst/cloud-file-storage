package dev.lqwd.cloudfilestorage.path_processor;

import dev.lqwd.cloudfilestorage.infrastructure.PathNormalizer;
import dev.lqwd.cloudfilestorage.infrastructure.PathValidator;
import dev.lqwd.cloudfilestorage.parser.PathParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class PathProcessor {

    private final PathNormalizer normalizer;
    private final PathParser parser;
    private final PathValidator validator;

    public ProcessedPath processResource(String rawPath) {
        String normalized = normalizer.normalize(rawPath);
        validator.validatePath(normalized);
        return parser.pars(normalized);
    }

    public ProcessedPath processDir(String rawPath) {
        String normalized = normalizer.normalize(rawPath);
        validator.validateDirPath(normalized);
        return parser.pars(normalized);
    }

    public ProcessedPath processFile(String rawPath) {
        String normalized = normalizer.normalize(rawPath);
        validator.validateFilePath(normalized);
        return parser.pars(normalized);
    }

}

