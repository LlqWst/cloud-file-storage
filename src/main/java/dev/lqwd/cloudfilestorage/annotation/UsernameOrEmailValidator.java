package dev.lqwd.cloudfilestorage.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.regex.Pattern;

public class UsernameOrEmailValidator implements ConstraintValidator<UsernameOrEmail, String> {

    private static final int MAX_EMAIL_LENGTH = 254;
    private static final Pattern LOGIN_PATTERN = Pattern.compile("^[a-zA-Z0-9 ~!#$%^&*()_=+/'\".-]{6,20}$");


    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank() || value.length() > MAX_EMAIL_LENGTH) {
            return false;
        }

        return LOGIN_PATTERN.matcher(value).matches() || EmailValidator.getInstance().isValid(value);
    }
}
