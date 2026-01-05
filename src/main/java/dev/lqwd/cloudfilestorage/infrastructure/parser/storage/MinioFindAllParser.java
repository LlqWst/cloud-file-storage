package dev.lqwd.cloudfilestorage.infrastructure.parser.storage;

import dev.lqwd.cloudfilestorage.dto.resource.ParsedResourceDto;
import dev.lqwd.cloudfilestorage.infrastructure.parser.PathParsHelper;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;


@Component
@RequiredArgsConstructor
public class MinioFindAllParser implements ResourceTypeParser, StorageResponseParser<Item> {

    private final PathParsHelper pathHelper;

    public ParsedResourceDto pars(Item item)  {
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