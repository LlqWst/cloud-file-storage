package dev.lqwd.cloudfilestorage.infrastructure.validator;

import dev.lqwd.cloudfilestorage.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class CredentialsValidator {

    @Value("${app.min.length.username}")
    private int minLengthUsername;

    @Value("${app.max.length.username}")
    private int maxLengthUsername;

    @Value("${app.min.length.password}")
    private int minLengthPassword;

    @Value("${app.max.length.password}")
    private int maxLengthPassword;

    public void validateCredentials(String username, String password) {
        if (isBlank(username) ||
            isBlank(password) ||
            isIncorrectUsernameLength(username) ||
            isIncorrectPasswordLength(password)) {

            throw new BadRequestException("Bad credentials");
        }
    }

    private boolean isBlank(String username) {
        return username == null || username.isBlank();
    }

    private boolean isIncorrectUsernameLength(String username) {
        return username.length() < minLengthUsername || username.length() > maxLengthUsername;
    }

    private boolean isIncorrectPasswordLength(String username) {
        return username.length() < minLengthPassword || username.length() > maxLengthPassword;
    }
}
