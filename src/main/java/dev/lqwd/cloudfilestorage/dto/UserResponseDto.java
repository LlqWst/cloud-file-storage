package dev.lqwd.cloudfilestorage.dto;

import lombok.Builder;


@Builder
public record UserResponseDto(
        String username
) {
}
