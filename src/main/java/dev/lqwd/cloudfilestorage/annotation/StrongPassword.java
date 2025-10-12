package dev.lqwd.cloudfilestorage.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = StrongPasswordValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface StrongPassword {


    String message() default "Password must be {min}-{max} characters long";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    int min() default 6;
    int max() default 64;

}
