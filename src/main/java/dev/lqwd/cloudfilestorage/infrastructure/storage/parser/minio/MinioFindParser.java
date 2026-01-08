package dev.lqwd.cloudfilestorage.infrastructure.storage.parser.minio;

import dev.lqwd.cloudfilestorage.infrastructure.path.PathParsHelper;
import dev.lqwd.cloudfilestorage.infrastructure.storage.parser.ParsedResource;
import dev.lqwd.cloudfilestorage.infrastructure.storage.parser.ResourceTypeParser;
import dev.lqwd.cloudfilestorage.infrastructure.storage.parser.StorageResponseParser;
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
    public ParsedResource parse(StatObjectResponse statObject) {
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
