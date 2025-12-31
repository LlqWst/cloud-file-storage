package dev.lqwd.cloudfilestorage.dto;

import dev.lqwd.cloudfilestorage.annotation.Username;
import dev.lqwd.cloudfilestorage.annotation.StrongPassword;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Auth request data transfer object")
public record RegistrationRequestDto(

        @Username
        @Schema(
                description = "Username",
                example = "Test1",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 5,
                maxLength = 20
        )
        String username,

        @StrongPassword
        @Schema(
                description = "User's password",
                example = "12345",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 5,
                maxLength = 20,
                pattern = "^[a-zA-Z0-9 ~!#$%^&*()_=+/'\".-]$"
        )
        String password
) {
}
