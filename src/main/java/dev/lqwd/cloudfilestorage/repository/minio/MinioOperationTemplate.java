package dev.lqwd.cloudfilestorage.repository.minio;

import dev.lqwd.cloudfilestorage.exception.InternalErrorException;
import dev.lqwd.cloudfilestorage.exception.MinioErrorException;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
@Slf4j
public class MinioOperationTemplate {

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
            log.debug("Path {} doesn't exists. Minio HTTP trace: {}", errorMessage, e.httpTrace(), e);
            return Optional.empty();
        } catch (Exception e) {
            throw identifyException(e, errorMessage);
        }
    }

    private RuntimeException identifyException(Exception e, String errorMessage) {
        if (e instanceof MinioException minioEx) {
            log.error("Minio HTTP trace: {}", minioEx.httpTrace(), minioEx);
            return new MinioErrorException("Minio error. " + errorMessage, minioEx);
        }
        return new InternalErrorException("Unexpected error. " + errorMessage, e);
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