package dev.lqwd.cloudfilestorage.exception;

public class MinioErrorException extends RuntimeException {

    public MinioErrorException(String message, Exception e) {
        super(message, e);
    }
}
