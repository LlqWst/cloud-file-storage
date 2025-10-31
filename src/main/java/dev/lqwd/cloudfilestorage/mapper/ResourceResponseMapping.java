package dev.lqwd.cloudfilestorage.mapper;

import dev.lqwd.cloudfilestorage.dto.ResourceResponseDTO;
import dev.lqwd.cloudfilestorage.exception.InternalErrorException;
import dev.lqwd.cloudfilestorage.model.ProcessedPath;
import dev.lqwd.cloudfilestorage.model.Type;
import io.minio.messages.Item;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ResourceResponseMapping {

    ResourceResponseDTO toResourceResponseDTO(ProcessedPath path);

    default ResourceResponseDTO toResourceResponseDTO(ProcessedPath path, Item item) {
        if (path == null || item == null) {
            throw new InternalErrorException("Error during mapping to ResponseDTO");
        }

        return new ResourceResponseDTO(
                path.path(),
                path.name(),
                (path.type() != Type.DIRECTORY) ? item.size() : null,
                path.type()
        );
    }

}
