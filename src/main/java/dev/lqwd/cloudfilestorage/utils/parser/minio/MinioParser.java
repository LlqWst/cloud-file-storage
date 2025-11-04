package dev.lqwd.cloudfilestorage.utils.parser.minio;

import dev.lqwd.cloudfilestorage.dto.resource.DirectoryResourceDTO;
import dev.lqwd.cloudfilestorage.dto.resource.FileResourceDTO;
import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDTO;
import dev.lqwd.cloudfilestorage.entity.Type;


public interface MinioParser {

    default ResourceResponseDTO getResourceResponseDTO(long size, Type type, String name, String normalizedPath) {
        return switch (type) {
            case FILE -> FileResourceDTO.builder()
                    .name(name)
                    .path(normalizedPath)
                    .size(size)
                    .type(type)
                    .build();
            case DIRECTORY -> DirectoryResourceDTO.builder()
                    .name(name)
                    .path(normalizedPath)
                    .type(type)
                    .build();
        };
    }
}
