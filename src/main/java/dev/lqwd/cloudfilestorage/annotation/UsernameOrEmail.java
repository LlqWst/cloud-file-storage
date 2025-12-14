package dev.lqwd.cloudfilestorage.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = UsernameOrEmailValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UsernameOrEmail {

    String message() default "Please provide username 5-20 characters long (char '@' not supported for username), or correct email";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
