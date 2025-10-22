package dev.lqwd.cloudfilestorage.exception;

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
