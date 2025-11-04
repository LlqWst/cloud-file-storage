package dev.lqwd.cloudfilestorage.repository.minio;

import dev.lqwd.cloudfilestorage.exception.BadRequestException;
import dev.lqwd.cloudfilestorage.exception.InternalErrorException;
import dev.lqwd.cloudfilestorage.exception.NotFoundException;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class MinioOperationTemplate {

    public <T> T execute(MinioOperation<T> operation,
                         String errorMessage) {
        try {
            return operation.execute();
        } catch (ErrorResponseException e) {
            log.warn("HTTP trace: {}", e.httpTrace(), e);
            throw new NotFoundException("Resource doesn't exists. " + errorMessage, e);
        } catch (MinioException e) {
            log.error("HTTP trace: {}", e.httpTrace(), e);
            throw new InternalErrorException("Minio error. " + errorMessage, e);
        } catch (Exception e) {
            log.error("{} : {}", errorMessage, e.getMessage(), e);
            throw new InternalErrorException("Unexpected error. " + errorMessage, e);
        }
    }

    public void execute(MinioVoidOperation operation,
                        String errorMessage) {
        try {
            operation.execute();
        } catch (ErrorResponseException e) {
            log.warn("HTTP trace: {}", e.httpTrace(), e);
            throw new BadRequestException("Resource doesn't exists. " + errorMessage, e);
        } catch (MinioException e) {
            log.error("HTTP trace: {}", e.httpTrace(), e);
            throw new InternalErrorException("Minio error. " + errorMessage, e);
        } catch (Exception e) {
            log.error("{} : {}", errorMessage, e.getMessage(), e);
            throw new InternalErrorException("Unexpected error. " + errorMessage, e);
        }
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