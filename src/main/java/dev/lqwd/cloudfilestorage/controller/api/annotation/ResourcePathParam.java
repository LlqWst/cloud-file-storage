package dev.lqwd.cloudfilestorage.controller.api.annotation;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Parameter(
        description = "The path to resource",
        required = true,
        examples = {
                @ExampleObject(
                        name = "test folder in root directory",
                        value = "test123/"
                ),
                @ExampleObject(
                        name = "test resource in root directory",
                        value = "file_test.txt"
                )
        }
)
public @interface ResourcePathParam {
}
