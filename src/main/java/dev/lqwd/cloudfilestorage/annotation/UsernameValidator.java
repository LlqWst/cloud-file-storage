package dev.lqwd.cloudfilestorage.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class UsernameValidator implements ConstraintValidator<Username, String> {

    private static final Pattern LOGIN_PATTERN = Pattern.compile("^[a-zA-Z0-9 ~!#$%^&*()_=+/'\".-]{5,20}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }

        return LOGIN_PATTERN.matcher(value).matches();
    }
}
