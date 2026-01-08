package dev.lqwd.cloudfilestorage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static dev.lqwd.cloudfilestorage.util.CredentialsParameters.*;
import static dev.lqwd.cloudfilestorage.util.RepeatableErrorMessage.*;


@Schema(description = "Auth request data transfer object")
public record RegistrationRequestDto(

        @NotBlank(message = USERNAME_IS_BLANK_ERROR_MESSAGE)
        @Size(min = minLengthUsername, max = maxLengthUsername, message = USERNAME_IS_INCORRECT_ERROR_MESSAGE)
        @Pattern(regexp = usernamePattern, message = USERNAME_HAS_INVALID_CHARS_ERROR_MESSAGE)
        @Schema(
                description = "Username",
                example = "Test1",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = minLengthUsername,
                maxLength = maxLengthUsername,
                pattern = usernamePattern
        )
        String username,

        @NotBlank(message = PASSWORD_IS_BLANK_ERROR_MESSAGE)
        @Size(min = minLengthPassword, max = maxLengthPassword, message = PASSWORD_IS_INCORRECT_ERROR_MESSAGE)
        @Pattern(regexp = passwordPattern, message = PASSWORD_HAS_SPACES_ERROR_MESSAGE)
        @Schema(
                description = "User's password",
                example = "12345",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = minLengthPassword,
                maxLength = maxLengthPassword,
                pattern = passwordPattern
        )
        String password
) {
}
