package dev.lqwd.cloudfilestorage.exception;

public class DataBaseException extends RuntimeException {
    public DataBaseException(String message) {
        super(message);
    }

    public DataBaseException(String message, Exception e) {
        super(message, e);
    }
}
