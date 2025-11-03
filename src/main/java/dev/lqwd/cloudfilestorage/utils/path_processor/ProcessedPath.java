package dev.lqwd.cloudfilestorage.utils.path_processor;

import dev.lqwd.cloudfilestorage.entity.Type;
import lombok.Builder;

@Builder
public record ProcessedPath(
        String requestedPath,
        String parentPath,
        String resourceName,
        Type type) {
}