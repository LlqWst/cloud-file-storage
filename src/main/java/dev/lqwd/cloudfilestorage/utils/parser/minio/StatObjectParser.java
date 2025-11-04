package dev.lqwd.cloudfilestorage.utils.parser.minio;

import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDTO;
import dev.lqwd.cloudfilestorage.utils.PathNormalizer;
import dev.lqwd.cloudfilestorage.utils.parser.AbstractParser;
import io.minio.StatObjectResponse;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;


@Component
public class StatObjectParser extends AbstractParser implements MinioParser {

    public StatObjectParser(PathNormalizer pathNormalizer) {
        super(pathNormalizer);
    }

    public ResourceResponseDTO pars(StatObjectResponse statObject) {
        String objectPath = statObject.object();
        Path path = Paths.get(objectPath);
        String requestedPath = removeUserDir(getParentPath(path));

        return getResourceResponseDTO(statObject.size(),
                getType(objectPath),
                getName(path),
                normalizeRootPath(requestedPath));
    }

}

