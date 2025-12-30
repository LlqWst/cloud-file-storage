package dev.lqwd.cloudfilestorage.dto.resource;

import dev.lqwd.cloudfilestorage.entity.Type;


public interface ResourceResponseDto {
    String path();

    String name();

    Type type();
}

