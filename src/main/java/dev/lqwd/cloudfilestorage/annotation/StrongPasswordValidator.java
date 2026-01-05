package dev.lqwd.cloudfilestorage.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    @Value("${app.min.length.password}")
    private int min;

    @Value("${app.max.length.password}")
    private int max;

    private static final String MESSAGE = "Password must be %d-%d characters long";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null ||
            value.isBlank() ||
            value.length() < min ||
            value.length() > max) {

            addViolation(context, MESSAGE.formatted(min, max));
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
