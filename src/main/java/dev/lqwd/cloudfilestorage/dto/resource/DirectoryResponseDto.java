package dev.lqwd.cloudfilestorage.dto.resource;

import dev.lqwd.cloudfilestorage.entity.Type;
import lombok.Builder;


@Builder
public record DirectoryResponseDto(
        String path,
        String name,
        Type type
) implements ResourceResponseDto {
}
