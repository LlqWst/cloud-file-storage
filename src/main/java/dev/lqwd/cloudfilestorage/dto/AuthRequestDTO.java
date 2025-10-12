package dev.lqwd.cloudfilestorage.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthRequestDTO(

        @NotBlank(message = "Login is required")
        String login,

        @NotBlank(message = "Password is required")
        String password
) {
}
