package dev.lqwd.cloudfilestorage.dto.resource;

import dev.lqwd.cloudfilestorage.entity.Type;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;


@Builder
@Schema(
        title = "DirectoryResponseDto",
        description = "Contains information about the directory"
)
public record DirectoryResponseDto(

        @Schema(
                description = "the path to the folder where the resource is located",
                example = "folder1/folder2/"
        )
        String path,

        @Schema(
                description = "Name of directory",
                example = "folder3"
        )
        String name,

        @Schema(
                description = "Type of directory",
                example = "DIRECTORY"
        )
        Type type

) implements ResourceResponseDto {
}
