package dev.lqwd.cloudfilestorage.utils.parser;

import dev.lqwd.cloudfilestorage.dto.resource.DirectoryResourceDTO;
import dev.lqwd.cloudfilestorage.dto.resource.FileResourceDTO;
import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDTO;
import dev.lqwd.cloudfilestorage.entity.Type;
import dev.lqwd.cloudfilestorage.utils.PathNormalizer;
import io.minio.messages.Item;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;


@Component
public class ItemParser extends AbstractParser {

    public ItemParser(PathNormalizer pathNormalizer) {
        super(pathNormalizer);
    }

    public ResourceResponseDTO pars(Item item) {
        Path path = Paths.get(item.objectName());
        String requestedPath = removeUserDir(getParentPath(path));
        Type type = getType(item.objectName());
        String normalizedPath = normalizeRootPath(requestedPath);

        return switch (type) {
            case FILE -> FileResourceDTO.builder()
                    .name(getName(path))
                    .path(normalizedPath)
                    .size(item.size())
                    .type(type)
                    .build();
            case DIRECTORY -> DirectoryResourceDTO.builder()
                    .name(getName(path))
                    .path(normalizedPath)
                    .type(type)
                    .build();
        };
    }

    private String removeUserDir(String fullPath){
        return fullPath.substring(fullPath.indexOf(SLASH)  + 1);
    }

}

