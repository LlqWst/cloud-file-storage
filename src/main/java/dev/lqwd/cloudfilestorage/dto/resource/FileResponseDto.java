package dev.lqwd.cloudfilestorage.dto.resource;

import dev.lqwd.cloudfilestorage.entity.Type;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;


@Builder
@Schema(
        title = "FileResponseDto",
        description = "Contains information about the file"
)
public record FileResponseDto(

        @Schema(
                description = "the path to the folder where the resource is located",
                example = "folder1/folder2/"
        )
        String path,

        @Schema(
                description = "Name of file",
                example = "test.txt"
        )
        String name,

        @Schema(
                description = "The file size in bytes. If the resource is a folder, this field is omitted.",
                example = "123"
        )
        Long size,

        @Schema(
                description = "Type of file",
                example = "FILE"
        )
        Type type

) implements ResourceResponseDto {}
