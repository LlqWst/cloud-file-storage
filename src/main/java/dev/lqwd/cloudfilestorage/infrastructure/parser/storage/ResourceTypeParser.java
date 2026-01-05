package dev.lqwd.cloudfilestorage.infrastructure.parser.storage;

import dev.lqwd.cloudfilestorage.dto.resource.ParsedResourceDto;
import dev.lqwd.cloudfilestorage.entity.Type;


public interface ResourceTypeParser {

    default ParsedResourceDto getParsedResourceDto(String fullPath,
                                                   long size,
                                                   Type type,
                                                   String name,
                                                   String normalizedPath) {

        return switch (type) {
            case FILE -> ParsedResourceDto.builder()
                    .fullPath(fullPath)
                    .name(name)
                    .path(normalizedPath)
                    .size(size)
                    .type(type)
                    .build();

            case DIRECTORY -> ParsedResourceDto.builder()
                    .fullPath(fullPath)
                    .name(name)
                    .path(normalizedPath)
                    .type(type)
                    .build();
        };
    }
}
