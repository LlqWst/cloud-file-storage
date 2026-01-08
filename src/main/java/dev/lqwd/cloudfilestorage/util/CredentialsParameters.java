package dev.lqwd.cloudfilestorage.util;

import lombok.experimental.UtilityClass;


@UtilityClass
public final class CredentialsParameters {

    public static final int minLengthUsername = 5;
    public static final int maxLengthUsername = 20;
    public static final String usernamePattern = "^[a-zA-Z0-9 ~!#$%^&*()_=+/'\".-]+$";

    public static final int minLengthPassword = 5;
    public static final int maxLengthPassword = 20;
    public static final String passwordPattern = "^\\S+$";

}
