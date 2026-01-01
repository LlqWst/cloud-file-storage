package dev.lqwd.cloudfilestorage.infrastructure.parser.minio;

import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDto;
import dev.lqwd.cloudfilestorage.infrastructure.parser.ResourceTypeParser;
import dev.lqwd.cloudfilestorage.infrastructure.parser.PathParsHelper;
import io.minio.StatObjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;


@Component
@RequiredArgsConstructor
public class StatObjectParser implements ResourceTypeParser {

    private final PathParsHelper pathHelper;

    public ResourceResponseDto pars(StatObjectResponse statObject) {
        String objectPath = statObject.object();
        Path path = Paths.get(objectPath);
        String requestedPath = pathHelper.removeUserDir(pathHelper.getParentPath(path));

        return getResourceResponseDto(statObject.size(),
                pathHelper.getType(objectPath),
                pathHelper.getName(path),
                requestedPath);
    }

}

