package dev.lqwd.cloudfilestorage.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = UsernameValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Username {

    String message() default "Please provide username 5-20 characters long";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
