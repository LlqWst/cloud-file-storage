package dev.lqwd.cloudfilestorage.parser;

import dev.lqwd.cloudfilestorage.dto.resource.DirectoryResponseDto;
import dev.lqwd.cloudfilestorage.dto.resource.FileResponseDto;
import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDto;
import dev.lqwd.cloudfilestorage.entity.Type;


public interface ResourceTypeParser {

    default ResourceResponseDto getResourceResponseDTO(long size, Type type, String name, String normalizedPath) {

        return switch (type) {
            case FILE -> FileResponseDto.builder()
                    .name(name)
                    .path(normalizedPath)
                    .size(size)
                    .type(type)
                    .build();
            case DIRECTORY -> DirectoryResponseDto.builder()
                    .name(name)
                    .path(normalizedPath)
                    .type(type)
                    .build();
        };
    }
}
