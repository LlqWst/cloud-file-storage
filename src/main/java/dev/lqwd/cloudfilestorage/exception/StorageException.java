package dev.lqwd.cloudfilestorage.exception;

public class StorageException extends RuntimeException {

    public StorageException(String message, Exception e) {
        super(message, e);
    }
}
