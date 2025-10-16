package dev.lqwd.cloudfilestorage.exception.user_validation;

import lombok.Getter;

@Getter
public class UserAlreadyExist extends RuntimeException {

    public UserAlreadyExist(String message) {
        super(message);
    }

    public UserAlreadyExist(String message, Exception e) {
        super(message, e);
    }

}
