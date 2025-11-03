package dev.lqwd.cloudfilestorage.utils.parser;

import dev.lqwd.cloudfilestorage.utils.PathNormalizer;
import dev.lqwd.cloudfilestorage.utils.path_processor.ProcessedPath;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;


@Component
public class PathParser extends AbstractParser {

    public PathParser(PathNormalizer pathNormalizer) {
        super(pathNormalizer);
    }

    public ProcessedPath pars(String normalizedPath) {
        Path path = Paths.get(normalizedPath);

        return ProcessedPath.builder()
                .requestedPath(normalizedPath)
                .resourceName(getName(path))
                .parentPath(getParentPath(path))
                .type(getType(normalizedPath))
                .build();
    }
}