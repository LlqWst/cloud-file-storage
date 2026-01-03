package dev.lqwd.cloudfilestorage.repository.storage.minio;

import dev.lqwd.cloudfilestorage.exception.InternalErrorException;
import dev.lqwd.cloudfilestorage.exception.StorageException;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;


@Component
@Slf4j
public class MinioOperationTemplate {

    private static final Set<String> KEYS_FOR_NOT_EXISTS = Set.of(
            "NoSuchKey",
            "NoSuchObject"
    );

    public <T> T execute(MinioOperation<T> operation, String errorMessage) {
        try {
            return operation.execute();
        } catch (Exception e) {
            throw identifyException(e, errorMessage);
        }
    }

    public void execute(MinioVoidOperation operation, String errorMessage) {
        try {
            operation.execute();
        } catch (Exception e) {
            throw identifyException(e, errorMessage);
        }
    }

    public <T> Optional<T> findResource(MinioOperation<T> operation, String errorMessage) {
        try {
           return Optional.ofNullable(operation.execute());
        } catch (ErrorResponseException e) {
            if (KEYS_FOR_NOT_EXISTS.contains(e.errorResponse().code())){
                log.debug("Path {} doesn't exists. Minio HTTP trace: {}", errorMessage, e.httpTrace(), e);
                return Optional.empty();
            }
            throw identifyException(e, errorMessage);
        } catch (Exception e) {
            throw identifyException(e, errorMessage);
        }
    }

    private RuntimeException identifyException(Exception e, String errorMessage) {
        if (e instanceof ErrorResponseException respExp) {
            log.error("Race condition happened. Minio HTTP trace: {}", respExp.httpTrace(), respExp);
            return MinioErrorResponse(errorMessage, respExp);
        }
        if (e instanceof MinioException minioEx) {
            return MinioErrorResponse(errorMessage, minioEx);
        }
        return new InternalErrorException("Unexpected error. " + errorMessage, e);
    }

    private static StorageException MinioErrorResponse(String errorMessage, MinioException minioEx) {
        log.error("Minio HTTP trace: {}", minioEx.httpTrace(), minioEx);
        return new StorageException("Minio error. " + errorMessage, minioEx);
    }

    @FunctionalInterface
    public interface MinioOperation<T> {
        T execute() throws Exception;
    }

    @FunctionalInterface
    public interface MinioVoidOperation {
        void execute() throws Exception;
    }

}