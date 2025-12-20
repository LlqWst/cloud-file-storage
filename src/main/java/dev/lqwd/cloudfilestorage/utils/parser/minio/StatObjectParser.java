package dev.lqwd.cloudfilestorage.utils.parser.minio;

import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDto;
import dev.lqwd.cloudfilestorage.utils.parser.PathParsHelper;
import io.minio.StatObjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;


@Component
@RequiredArgsConstructor
public class StatObjectParser implements MinioParser {

    private final PathParsHelper pathHelper;

    public ResourceResponseDto pars(StatObjectResponse statObject) {
        String objectPath = statObject.object();
        Path path = Paths.get(objectPath);
        String requestedPath = pathHelper.removeUserDir(pathHelper.getParentPath(path));

        return getResourceResponseDTO(statObject.size(),
                pathHelper.getType(objectPath),
                pathHelper.getName(path),
                requestedPath);
    }

}

