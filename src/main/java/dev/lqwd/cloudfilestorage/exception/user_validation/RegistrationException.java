package dev.lqwd.cloudfilestorage.exception.user_validation;

import lombok.Getter;

@Getter
public class RegistrationException extends RuntimeException {

    public RegistrationException(String message) {
        super(message);
    }

    public RegistrationException(String message, Exception e) {
        super(message, e);
    }

}
