package dev.lqwd.cloudfilestorage.service.auth;

import dev.lqwd.cloudfilestorage.exception.StorageException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

@Slf4j
@UtilityClass
public final class RegistrationOperationExecutor {

    public static <T> T executeWithLog(String operationName, Supplier<T> operation) {
        try {
            logStart(operationName);
            T result = operation.get();
            logComplete(operationName);
            return result;
        } catch (Exception e) {
            logFailed(operationName, e);
            throw e;
        }
    }

    public static void executeWithCompensation(
            String operationName,
            Runnable operation,
            Runnable compensation) {

        try {
            logStart(operationName);
            operation.run();
            logComplete(operationName);
        } catch (Exception e) {
            logFailed(operationName, e);

            try {
                log.info("Executing compensation...");
                compensation.run();
            } catch (Exception compEx) {
                log.error("Compensation failed for {}", operationName, compEx);
            }

            throw new StorageException(operationName + " failed", e);
        }
    }

    public static void executeSuppressingErrors(
            String operationName,
            Runnable operation) {

        try {
            operation.run();
            logComplete(operationName);
        } catch (Exception e) {
            log.warn("{} failed (suppressed): {}", operationName, e.getMessage());
        }
    }

    private static void logComplete(String operationName) {
        log.info("Completed: {}", operationName);
    }

    private static void logStart(String operationName) {
        log.info("Starting: {}", operationName);
    }

    private static void logFailed(String operationName, Exception e) {
        log.error("Failed {}: {}", operationName, e.getMessage(), e);
    }

}
