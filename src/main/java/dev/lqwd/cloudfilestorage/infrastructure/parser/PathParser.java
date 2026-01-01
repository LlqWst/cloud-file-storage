package dev.lqwd.cloudfilestorage.infrastructure.parser;

import dev.lqwd.cloudfilestorage.infrastructure.path_processor.ProcessedPath;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;


@Component
@RequiredArgsConstructor
public class PathParser  {

    private final PathParsHelper pathHelper;

    public ProcessedPath pars(String normalizedPath) {
        Path path = Paths.get(normalizedPath);

        return ProcessedPath.builder()
                .requestedPath(normalizedPath)
                .resourceName(pathHelper.getName(path))
                .parentPath(pathHelper.getParentPath(path))
                .type(pathHelper.getType(normalizedPath))
                .build();
    }
}