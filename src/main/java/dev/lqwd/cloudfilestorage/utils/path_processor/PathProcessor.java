package dev.lqwd.cloudfilestorage.utils.path_processor;

import dev.lqwd.cloudfilestorage.service.minio.UserDirectoryProvider;
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
    private final UserDirectoryProvider provider;

    public ProcessedPath processResource(String rawPath, long id) {
        String normalized = getNormalizedWithUserDir(rawPath, id);
        validator.validatePath(normalized);
        return pars(normalized);
    }

    public ProcessedPath processDir(String rawPath, long id) {
        String normalized = getNormalizedWithUserDir(rawPath, id);
        validator.validateDirPath(normalized);
        return pars(normalized);
    }

    public ProcessedPath processFile(String rawPath, long id) {
        String normalized = getNormalizedWithUserDir(rawPath, id);
        validator.validateFilePath(normalized);
        return pars(normalized);
    }

    private String getNormalizedWithUserDir(String rawPath, long id) {
        String pathWithUserDir = provider.getPathWithUserDir(rawPath, id);
        return normalizer.normalize(pathWithUserDir);
    }

    private ProcessedPath pars(String normalized) {
        return parser.pars(normalized);
    }

}

