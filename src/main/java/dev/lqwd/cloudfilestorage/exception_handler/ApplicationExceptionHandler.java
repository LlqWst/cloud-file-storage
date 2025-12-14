package dev.lqwd.cloudfilestorage.exception_handler;

import dev.lqwd.cloudfilestorage.dto.ErrorResponseDto;
import dev.lqwd.cloudfilestorage.exception.BadRequestException;
import dev.lqwd.cloudfilestorage.exception.AlreadyExistException;
import dev.lqwd.cloudfilestorage.exception.InternalErrorException;
import dev.lqwd.cloudfilestorage.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;


import java.util.stream.Collectors;


@RestControllerAdvice
@Slf4j
public class ApplicationExceptionHandler extends BaseHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(
            MethodArgumentNotValidException e) {

        log.error("Exception occurred:  {}", e.getMessage(), e);
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));

        return buildBadRequestResponse(message);
    }

    @ExceptionHandler(AlreadyExistException.class)
    public ResponseEntity<ErrorResponseDto> handleUserAlreadyExistsException(AlreadyExistException e) {

        log.error("Exception occurred:  {}", e.getMessage(), e);
        return buildConflictResponse(e.getMessage());
    }

    @ExceptionHandler({BadRequestException.class, MissingServletRequestParameterException.class,
            HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<ErrorResponseDto> handleBadRequestException(Exception e) {

        log.warn("Exception occurred:  {}", e.getMessage(), e);
        return buildBadRequestResponse(e.getMessage());
    }

    @ExceptionHandler({NotFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<ErrorResponseDto> handleNotFoundException(Exception e) {

        log.warn("Exception occurred:  {}", e.getMessage(), e);
        return buildNotFoundResponse(e.getMessage());
    }

    @ExceptionHandler({Exception.class, InternalErrorException.class})
    public ResponseEntity<ErrorResponseDto> handleUniversalException(Exception e) {

        log.error("Exception occurred:  {}", e.getMessage(), e);
        return buildInternalServerErrorResponse("Internal error exception");
    }

}

