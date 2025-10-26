package dev.lqwd.cloudfilestorage.utils;

import dev.lqwd.cloudfilestorage.exception.BadRequestException;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@NoArgsConstructor
public class Validator {

    public static final int MIN_LENGTH = 6;
    public static final int MAX_LENGTH_USERNAME = 20;
    public static final int MAX_LENGTH_PASSWORD = 64;

    public void validatePath(String path){
        if (path == null || path.trim().isBlank()) {
            throw new BadRequestException("Folder path cannot be empty");
        }
    }

    public void validateCredentials(String username, String password) {
        if (isBlank(username) || isIncorrectUsernameLength(username)
            || isBlank(password) || isIncorrectPasswordLength(password)) {

            throw new BadRequestException("Bad credentials");
        }
    }

    private boolean isBlank(String username) {
        return username == null || username.isBlank();
    }

    private boolean isIncorrectUsernameLength(String username) {
        return username.length() < MIN_LENGTH || username.length() > MAX_LENGTH_USERNAME;
    }

    private boolean isIncorrectPasswordLength(String username) {
        return username.length() < MIN_LENGTH || username.length() > MAX_LENGTH_PASSWORD;
    }
}
