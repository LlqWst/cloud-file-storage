package dev.lqwd.cloudfilestorage.utils.parser.minio;

import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDTO;
import dev.lqwd.cloudfilestorage.utils.PathNormalizer;
import dev.lqwd.cloudfilestorage.utils.parser.AbstractParser;
import io.minio.messages.Item;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;


@Component
public class ItemParser extends AbstractParser implements MinioParser {

    public ItemParser(PathNormalizer pathNormalizer) {
        super(pathNormalizer);
    }

    public ResourceResponseDTO pars(Item item)  {
        Path path = Paths.get(item.objectName());
        String requestedPath = removeUserDir(getParentPath(path));

        return getResourceResponseDTO(
                item.size(),
                getType(item.objectName()),
                getName(path),
                normalizeRootPath(requestedPath));
    }

}