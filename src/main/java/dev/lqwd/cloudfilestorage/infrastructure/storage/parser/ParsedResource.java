package dev.lqwd.cloudfilestorage.infrastructure.storage.parser;

import dev.lqwd.cloudfilestorage.entity.Type;
import lombok.Builder;


@Builder
public record ParsedResource(

        String fullPath,

        String path,

        String name,

        Long size,

        Type type

) {
}
