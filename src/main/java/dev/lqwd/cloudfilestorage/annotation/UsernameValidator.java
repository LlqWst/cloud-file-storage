package dev.lqwd.cloudfilestorage.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class UsernameValidator implements ConstraintValidator<Username, String> {

    @Value("${app.min.length.username}")
    private int min;

    @Value("${app.max.length.username}")
    private int max;

    @Value("${app.pattern.username}")
    private String pattern;

    private static final String MESSAGE = "Please provide username %d-%d chars long (valid chars: %s)";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        Pattern loginPattern = Pattern.compile(pattern);

        if (value == null ||
            value.isBlank() ||
            value.length() < min ||
            value.length() > max ||
            !loginPattern.matcher(value).matches()) {

            addViolation(context, MESSAGE.formatted(min, max, pattern));
            return false;
        }
        return true;
    }

    private void addViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }

}
