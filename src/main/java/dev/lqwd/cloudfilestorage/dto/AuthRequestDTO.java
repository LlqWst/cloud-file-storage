package dev.lqwd.cloudfilestorage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequestDTO(

        @Size(min = 6, max = 20, message = "Username must be between 6 and 20 characters")
        @NotBlank(message = "username is required")
        String username,

        @Size(min = 6, max = 20, message = "password must be between 6 and 20 characters")
        @NotBlank(message = "Password is required")
        String password
) {
}
