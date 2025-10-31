package dev.lqwd.cloudfilestorage.model;

import lombok.Builder;

@Builder
public record ProcessedPath(
        String requestedPath,
        String path,
        String name,
        Type type) {
}
