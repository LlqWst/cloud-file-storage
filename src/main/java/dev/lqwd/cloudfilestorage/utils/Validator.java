package dev.lqwd.cloudfilestorage.utils;

import dev.lqwd.cloudfilestorage.exception.BadRequestException;
import lombok.experimental.UtilityClass;


@UtilityClass
public final class Validator {

    public static final int MIN_LENGTH = 6;
    public static final int MAX_LENGTH = 20;

    public static void validateCredentials(String username, String password) {

        if (isBlank(username) || isIncorrectLength(username)
            || isBlank(password) || isIncorrectLength(password)) {

            throw new BadRequestException("Bad credentials");
        }
    }

    private static boolean isBlank(String username) {
        return username == null || username.isBlank();
    }

    private static boolean isIncorrectLength(String username) {
        return username.length() < MIN_LENGTH || username.length() > MAX_LENGTH;
    }
}
