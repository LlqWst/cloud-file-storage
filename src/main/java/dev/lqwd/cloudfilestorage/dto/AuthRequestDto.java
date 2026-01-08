package dev.lqwd.cloudfilestorage.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static dev.lqwd.cloudfilestorage.util.CredentialsParameters.*;
import static dev.lqwd.cloudfilestorage.util.RepeatableErrorMessage.BAD_CREDENTIALS_ERROR_MESSAGE;

public record AuthRequestDto(

        @NotBlank(message = BAD_CREDENTIALS_ERROR_MESSAGE)
        @Size(min = minLengthUsername, max = maxLengthUsername, message = BAD_CREDENTIALS_ERROR_MESSAGE)
        String username,

        @NotBlank(message = BAD_CREDENTIALS_ERROR_MESSAGE)
        @Size(min = minLengthPassword, max = maxLengthPassword, message = BAD_CREDENTIALS_ERROR_MESSAGE)
        @Pattern(regexp = passwordPattern, message = BAD_CREDENTIALS_ERROR_MESSAGE)
        String password
) {
}
