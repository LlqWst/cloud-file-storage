package dev.lqwd.cloudfilestorage.dto.resource;

import dev.lqwd.cloudfilestorage.entity.Type;
import lombok.Builder;

@Builder
public record FileResourceDto(
        String path,
        String name,
        Long size,
        Type type
) implements ResourceResponseDto {}
