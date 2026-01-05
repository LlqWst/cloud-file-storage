package dev.lqwd.cloudfilestorage.infrastructure.parser.storage;

import dev.lqwd.cloudfilestorage.infrastructure.parser.PathParsHelper;
import io.minio.StatObjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;


@Component
@RequiredArgsConstructor
public class MinioFindParser implements ResourceTypeParser, StorageResponseParser<StatObjectResponse> {

    private final PathParsHelper pathHelper;

    @Override
    public ParsedResource pars(StatObjectResponse statObject) {
        String fullPath = statObject.object();
        Path path = Paths.get(fullPath);
        String requestedPath = pathHelper.removeUserDir(pathHelper.getParentPath(path));

        return getParsedResourceDto(
                fullPath,
                statObject.size(),
                pathHelper.getType(fullPath),
                pathHelper.getName(path),
                requestedPath);
    }

}
