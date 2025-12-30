package dev.lqwd.cloudfilestorage.parser.minio;

import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDto;
import dev.lqwd.cloudfilestorage.parser.ResourceTypeParser;
import dev.lqwd.cloudfilestorage.parser.PathParsHelper;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;


@Component
@RequiredArgsConstructor
public class ItemParser implements ResourceTypeParser {

    private final PathParsHelper pathHelper;


    public ResourceResponseDto pars(Item item)  {
        Path path = Paths.get(item.objectName());
        String requestedPath = pathHelper.removeUserDir(pathHelper.getParentPath(path));

        return getResourceResponseDto(
                item.size(),
                pathHelper.getType(item.objectName()),
                pathHelper.getName(path),
                requestedPath);
    }

}