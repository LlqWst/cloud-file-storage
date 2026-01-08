package dev.lqwd.cloudfilestorage.infrastructure.storage.parser;

import dev.lqwd.cloudfilestorage.entity.Type;


public interface ResourceTypeParser {

    default ParsedResource getParsedResourceDto(String fullPath,
                                                long size,
                                                Type type,
                                                String name,
                                                String normalizedPath) {

        return switch (type) {
            case FILE -> ParsedResource.builder()
                    .fullPath(fullPath)
                    .name(name)
                    .path(normalizedPath)
                    .size(size)
                    .type(type)
                    .build();

            case DIRECTORY -> ParsedResource.builder()
                    .fullPath(fullPath)
                    .name(name)
                    .path(normalizedPath)
                    .type(type)
                    .build();
        };
    }
}
