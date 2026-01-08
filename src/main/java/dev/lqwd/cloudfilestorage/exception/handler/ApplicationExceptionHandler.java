package dev.lqwd.cloudfilestorage.exception.handler;

import dev.lqwd.cloudfilestorage.dto.ErrorResponseDto;
import dev.lqwd.cloudfilestorage.dto.property.MultipartProperties;
import dev.lqwd.cloudfilestorage.exception.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.FileCountLimitExceededException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

import static dev.lqwd.cloudfilestorage.util.RepeatableErrorMessage.INTERNAL_ERROR_MESSAGE;
import static dev.lqwd.cloudfilestorage.util.RepeatableErrorMessage.METHOD_NOT_ALLOWED_ERROR_MESSAGE;


@RestControllerAdvice
@AllArgsConstructor
@Slf4j
public class ApplicationExceptionHandler extends BaseHandler {

    private final MultipartProperties properties;

    private static final String LOG_TEMPLATE_MESSAGE = "Exception occurred:  {}";
    private static final String MAX_UPLOAD_SIZE_TEMPLATE = """
            The maximum file upload size has been exceeded.
            The maximum size of a single file: %s.
            Maximum download size: %s
            """;
    private static final String MAX_UPLOAD_FILES_TEMPLATE = """
            The maximum number of files to upload has been exceeded.
            Maximum number of files per request - %d
            """;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(
            MethodArgumentNotValidException e) {

        log.warn(LOG_TEMPLATE_MESSAGE, e.getMessage(), e);

        return buildBadRequestResponse(e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "))
        );
    }

    @ExceptionHandler(AlreadyExistException.class)
    public ResponseEntity<ErrorResponseDto> handleUserAlreadyExistsException(AlreadyExistException e) {

        log.error(LOG_TEMPLATE_MESSAGE, e.getMessage(), e);
        return buildConflictResponse(e.getMessage());
    }

    @ExceptionHandler({
            BadRequestException.class,
            MissingServletRequestParameterException.class,
            MissingServletRequestPartException.class
    })
    public ResponseEntity<ErrorResponseDto> handleBadRequestException(Exception e) {

        log.warn(LOG_TEMPLATE_MESSAGE, e.getMessage(), e);

        if (e instanceof MissingServletRequestParameterException ex) {
            return buildBadRequestResponse("Missing required parameter: " + ex.getParameterName());
        } else if (e instanceof MissingServletRequestPartException ex) {
            return buildBadRequestResponse("Missing required parameter: " + ex.getRequestPartName());
        }
        return buildBadRequestResponse(e.getMessage());

    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto> handleNullContentType(HttpMediaTypeNotSupportedException e) {

        log.warn(LOG_TEMPLATE_MESSAGE, e.getMessage(), e);
        if (e.getContentType() == null) {
            return buildBadRequestResponse("Missing body type");
        }
        return buildBadRequestResponse("Incorrect body Type");
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponseDto> handleMultipartException(MultipartException e) {

        log.warn(LOG_TEMPLATE_MESSAGE, e.getMessage(), e);
        return buildBadRequestResponse(MAX_UPLOAD_SIZE_TEMPLATE
                .formatted(properties.maxFileSize(), properties.maxRequestSize()));
    }

    @ExceptionHandler({
            FileCountLimitExceededException.class,
            MultipartException.class
    })
    public ResponseEntity<ErrorResponseDto> handleFileCountLimitException(Exception e) {

        log.warn(LOG_TEMPLATE_MESSAGE, e.getMessage(), e);
        if (isInstanceOf(e, FileCountLimitExceededException.class)) {
            return buildBadRequestResponse(MAX_UPLOAD_FILES_TEMPLATE.formatted(properties.maxFilesCount()));
        }
        return buildBadRequestResponse("Failed to process uploaded");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {

        log.warn(LOG_TEMPLATE_MESSAGE, e.getMessage(), e);
        return buildNotAllowed(METHOD_NOT_ALLOWED_ERROR_MESSAGE + e.getMethod());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFoundException(Exception e) {

        log.warn(LOG_TEMPLATE_MESSAGE, e.getMessage(), e);
        return buildNotFoundResponse(e.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNoResourceFoundException(Exception e) {

        log.warn(LOG_TEMPLATE_MESSAGE, e.getMessage(), e);
        return buildNotFoundResponse("Not Found");
    }

    @ExceptionHandler({
            Exception.class,
            InternalErrorException.class,
            StorageException.class,
            SerializationException.class,
            IllegalStateException.class
    })
    public ResponseEntity<ErrorResponseDto> handleUniversalException(Exception e) {

        log.error(LOG_TEMPLATE_MESSAGE, e.getMessage(), e);
        return buildInternalServerErrorResponse(INTERNAL_ERROR_MESSAGE);
    }

}

