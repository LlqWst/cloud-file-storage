package dev.lqwd.cloudfilestorage.infrastructure.parser.storage;

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
