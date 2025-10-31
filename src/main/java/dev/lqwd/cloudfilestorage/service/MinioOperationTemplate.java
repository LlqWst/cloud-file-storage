package dev.lqwd.cloudfilestorage.service;

import dev.lqwd.cloudfilestorage.exception.InternalErrorException;
import io.minio.errors.MinioException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@AllArgsConstructor
public class MinioOperationTemplate {

    public <T> T execute(MinioOperation<T> operation,
                         String minioErrorMessage, String generalErrorMessage) {
        try {
            return operation.execute();
        } catch (MinioException e) {
            log.error("HTTP trace: {}", e.httpTrace(), e);
            throw new InternalErrorException(minioErrorMessage, e);
        } catch (Exception e) {
            log.error("{} : {}", generalErrorMessage, e.getMessage(), e);
            throw new InternalErrorException(generalErrorMessage, e);
        }
    }

    public void execute(MinioVoidOperation operation,
                        String minioErrorMessage, String generalErrorMessage) {
        try {
            operation.execute();
        } catch (MinioException e) {
            log.error("HTTP trace: {}", e.httpTrace(), e);
            throw new InternalErrorException(minioErrorMessage, e);
        } catch (Exception e) {
            log.error("{} : {}", generalErrorMessage, e.getMessage(), e);
            throw new InternalErrorException(generalErrorMessage, e);
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
