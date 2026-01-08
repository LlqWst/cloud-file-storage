package dev.lqwd.cloudfilestorage.infrastructure.storage.parser.minio;

import dev.lqwd.cloudfilestorage.infrastructure.path.PathParsHelper;
import dev.lqwd.cloudfilestorage.infrastructure.storage.parser.ParsedResource;
import dev.lqwd.cloudfilestorage.infrastructure.storage.parser.ResourceTypeParser;
import dev.lqwd.cloudfilestorage.infrastructure.storage.parser.StorageResponseParser;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;


@Component
@RequiredArgsConstructor
public class MinioFindAllParser implements ResourceTypeParser, StorageResponseParser<Item> {

    private final PathParsHelper pathHelper;

    @Override
    public ParsedResource parse(Item item)  {
        String fullPath = item.objectName();
        Path path = Paths.get(fullPath);
        String requestedPath = pathHelper.removeUserDir(pathHelper.getParentPath(path));

        return getParsedResourceDto(
                fullPath,
                item.size(),
                pathHelper.getType(item.objectName()),
                pathHelper.getName(path),
                requestedPath);
    }

}