package dev.lqwd.cloudfilestorage.dto.resource;

import dev.lqwd.cloudfilestorage.entity.Type;
import lombok.Builder;


@Builder
public record ParsedResourceDto(

        String fullPath,

        String path,

        String name,

        Long size,

        Type type

) {
}
