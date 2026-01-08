package dev.lqwd.cloudfilestorage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;


@Builder
@Schema(description = "Username response transfer object")
public record UserResponseDto(

        @Schema(
                description = "Username",
                example = "name_test"
        )
        String username
) {
}
