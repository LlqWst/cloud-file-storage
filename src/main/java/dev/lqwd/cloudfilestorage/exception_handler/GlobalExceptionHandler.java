package dev.lqwd.cloudfilestorage.exception_handler;

import dev.lqwd.cloudfilestorage.dto.ErrorResponseDTO;
import dev.lqwd.cloudfilestorage.exception.BadRequestException;
import dev.lqwd.cloudfilestorage.exception.user_validation.AuthException;
import dev.lqwd.cloudfilestorage.exception.user_validation.RegistrationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


import java.util.stream.Collectors;


@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(
            MethodArgumentNotValidException e) {

        log.error("Exception occurred:  {}", e.getMessage(), e);

        String message = e.getBindingResult().getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(message);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserAlreadyExistsException(RegistrationException e) {

        log.error("Exception occurred:  {}", e.getMessage(), e);

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(e.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserAuthExceptionException(Exception e) {

        log.warn("Exception occurred:  {}", e.getMessage(), e);

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(e.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadRequestException(Exception e) {

        log.warn("Exception occurred:  {}", e.getMessage(), e);

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(e.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleUniversalException(Exception e) {

        log.error("Exception occurred:  {}", e.getMessage(), e);

        ErrorResponseDTO errorResponse = new ErrorResponseDTO("Internal error");
        return ResponseEntity.internalServerError().body(errorResponse);
    }

}

