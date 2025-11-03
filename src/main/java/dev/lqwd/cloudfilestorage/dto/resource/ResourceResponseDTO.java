package dev.lqwd.cloudfilestorage.dto.resource;


import dev.lqwd.cloudfilestorage.entity.Type;

public interface ResourceResponseDTO {
    String path();
    String name();
    Type type();
}

