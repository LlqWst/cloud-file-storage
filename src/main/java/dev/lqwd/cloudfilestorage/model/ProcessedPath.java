package dev.lqwd.cloudfilestorage.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class ProcessedPath {

    private final String fullPath;
    private final String parent;
    private final String name;
}
