package dev.lqwd.cloudfilestorage.dto;


import dev.lqwd.cloudfilestorage.annotation.UsernameOrEmail;
import dev.lqwd.cloudfilestorage.annotation.StrongPassword;

public record RegistrationRequestDTO (

        @UsernameOrEmail
        String username,

        @StrongPassword
        String password
) {
}
