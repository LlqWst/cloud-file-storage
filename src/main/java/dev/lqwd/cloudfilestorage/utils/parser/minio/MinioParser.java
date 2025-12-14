package dev.lqwd.cloudfilestorage.utils.parser.minio;

import dev.lqwd.cloudfilestorage.dto.resource.DirectoryResourceDto;
import dev.lqwd.cloudfilestorage.dto.resource.FileResourceDto;
import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDto;
import dev.lqwd.cloudfilestorage.entity.Type;


public interface MinioParser {

    default ResourceResponseDto getResourceResponseDTO(long size, Type type, String name, String normalizedPath) {
        return switch (type) {
            case FILE -> FileResourceDto.builder()
                    .name(name)
                    .path(normalizedPath)
                    .size(size)
                    .type(type)
                    .build();
            case DIRECTORY -> DirectoryResourceDto.builder()
                    .name(name)
                    .path(normalizedPath)
                    .type(type)
                    .build();
        };
    }
}
