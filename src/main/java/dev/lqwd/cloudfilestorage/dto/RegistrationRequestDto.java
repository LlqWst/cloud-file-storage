package dev.lqwd.cloudfilestorage.dto;

import dev.lqwd.cloudfilestorage.annotation.Username;
import dev.lqwd.cloudfilestorage.annotation.StrongPassword;


public record RegistrationRequestDto(

        @Username
        String username,

        @StrongPassword
        String password
) {
}
