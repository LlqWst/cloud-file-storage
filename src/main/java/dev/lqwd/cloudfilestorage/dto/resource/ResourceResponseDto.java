package dev.lqwd.cloudfilestorage.dto.resource;

import dev.lqwd.cloudfilestorage.entity.Type;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        title = "ResourceResponseDto",
        description = "interface for both types of resources, containing general information about the resource"
)
public interface ResourceResponseDto {

    @Schema(
            description = "the path to the folder where the resource is located",
            example = "folder1/folder2/"
    )
    String path();

    @Schema(
            description = "Name of resource",
            example = "folder3"
    )
    String name();

    @Schema(
            description = "Type of resource DIRECTORY or FILE",
            example = "DIRECTORY"
    )
    Type type();
}

