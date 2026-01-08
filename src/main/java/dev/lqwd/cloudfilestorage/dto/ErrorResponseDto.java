package dev.lqwd.cloudfilestorage.dto;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "An error message transfer object")
public record ErrorResponseDto(

        @Schema(
                description = "Error message"
                //example = "Internal error"
        )
        String message
) {
}
