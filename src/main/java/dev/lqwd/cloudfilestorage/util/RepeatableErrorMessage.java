package dev.lqwd.cloudfilestorage.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class RepeatableErrorMessage {

    public static final String INTERNAL_ERROR_MESSAGE = "Internal error";
    public static final String BAD_CREDENTIALS_ERROR_MESSAGE = "Bad credentials";
    public static final String METHOD_NOT_ALLOWED_ERROR_MESSAGE = "Method is not allowed: ";


    public static final String USER_ALREADY_EXISTS_ERROR_MESSAGE = "User already exists";
    public static final String USERNAME_IS_BLANK_ERROR_MESSAGE = "Username is required";
    public static final String USERNAME_IS_INCORRECT_ERROR_MESSAGE = "Please provide username 5-20 chars long";
    public static final String USERNAME_HAS_INVALID_CHARS_ERROR_MESSAGE =
            "Invalid username chars. Please use: a-zA-Z0-9 ~!#$%^&*()_=+/'\".-";


    public static final String PASSWORD_IS_BLANK_ERROR_MESSAGE = "Password is required";
    public static final String PASSWORD_IS_INCORRECT_ERROR_MESSAGE = "Please provide password 5-20 chars long";
    public static final String PASSWORD_HAS_SPACES_ERROR_MESSAGE = "Password cannot contains spaces";


    public static final String RESOURCE_EXCEEDED_LENGTH_NAME_ERROR_MESSAGE =
            "The resource bucketName '%s' exceeded max allowed bucketName length %d";
    public static final String NOT_DIRECTORY_ERROR_MESSAGE = "Resource is not a directory: directory should end with '/'";
    public static final String RESOURCE_ALREADY_EXISTS_ERROR_MESSAGE = "Resource already exists: ";
    public static final String PARENT_PATH_NOT_EXISTS_ERROR_MESSAGE = "Parent path doesn't exist: ";
    public static final String RESOURCE_NOT_EXISTS_ERROR_MESSAGE = "Resource doesn't exists: ";
    public static final String RESOURCE_NAME_IS_EMPTY_ERROR_MESSAGE = "Resource bucketName is empty or equals '/'";
    public static final String RESOURCE_INCORRECT_NAME_ERROR_MESSAGE =
            "Please enter a resource bucketName that doesn't include any of these chars: ";
    public static final String MOVE_TO_ITSELF_ERROR_MESSAGE = "You can't move resource to itself";
}

