package dev.lqwd.cloudfilestorage.exception.handler;

import dev.lqwd.cloudfilestorage.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public abstract class BaseHandler {

    protected ResponseEntity<ErrorResponseDto> buildBadRequestResponse(String message) {
        return ResponseEntity
                .badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(getBody(message));
    }

    protected ResponseEntity<ErrorResponseDto> buildConflictResponse(String message) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(getBody(message));
    }


    protected ResponseEntity<ErrorResponseDto> buildNotAllowed(String message) {
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(getBody(message));
    }

    protected ResponseEntity<ErrorResponseDto> buildNotFoundResponse(String message) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(getBody(message));
    }

    protected ResponseEntity<ErrorResponseDto> buildInternalServerErrorResponse(String message) {
        return ResponseEntity
                .internalServerError()
                .contentType(MediaType.APPLICATION_JSON)
                .body(getBody(message));
    }

    private static ErrorResponseDto getBody(String message) {
        return new ErrorResponseDto(message);
    }

    protected <T extends Throwable> boolean isInstanceOf(Exception e, Class<T> clazz) {
        Throwable cause = e;
        while (cause != null) {
            if (clazz.isInstance(cause)) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

}
