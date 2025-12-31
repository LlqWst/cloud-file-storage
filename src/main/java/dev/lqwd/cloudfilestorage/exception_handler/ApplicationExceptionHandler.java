package dev.lqwd.cloudfilestorage.exception_handler;

import dev.lqwd.cloudfilestorage.dto.ErrorResponseDto;
import dev.lqwd.cloudfilestorage.exception.BadRequestException;
import dev.lqwd.cloudfilestorage.exception.AlreadyExistException;
import dev.lqwd.cloudfilestorage.exception.InternalErrorException;
import dev.lqwd.cloudfilestorage.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.FileCountLimitExceededException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;


@RestControllerAdvice
@Slf4j
public class ApplicationExceptionHandler extends BaseHandler {

    private final String MAX_UPLOAD_SIZE_MESSAGE =
            """ 
                    The maximum file upload size has been exceeded.'
                    The maximum size of a single file: %s.
                    Maximum download size: %s
                    """.formatted(maxFileSize, maxRequestSize);

    private final String MAX_UPLOAD_FILES =
            """ 
                    The maximum number of files to upload has been exceeded.
                    Maximum number of files per request - %d
                    """.formatted(maxFilesCount);

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

    @ExceptionHandler({BadRequestException.class,
            MissingServletRequestParameterException.class,
            HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<ErrorResponseDto> handleBadRequestException(Exception e) {

        log.warn("Exception occurred:  {}", e.getMessage(), e);
        return buildBadRequestResponse(e.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponseDto> handleMultipartException(MultipartException e) {

        log.warn("Exception occurred:  {}", e.getMessage(), e);
        return buildBadRequestResponse(MAX_UPLOAD_SIZE_MESSAGE);
    }

    @ExceptionHandler({FileCountLimitExceededException.class,
            MultipartException.class})
    public ResponseEntity<ErrorResponseDto> handleFileCountLimitException(Exception e) {

        log.warn("Exception occurred:  {}", e.getMessage(), e);
        if (isInstanceOf(e, FileCountLimitExceededException.class)) {
            return buildBadRequestResponse(MAX_UPLOAD_FILES);
        }
        return buildBadRequestResponse(e.getMessage());
    }

    @ExceptionHandler({NotFoundException.class,
            NoResourceFoundException.class})
    public ResponseEntity<ErrorResponseDto> handleNotFoundException(Exception e) {

        log.warn("Exception occurred:  {}", e.getMessage(), e);
        return buildNotFoundResponse(e.getMessage());
    }

    @ExceptionHandler({Exception.class,
            InternalErrorException.class,
            SerializationException.class})
    public ResponseEntity<ErrorResponseDto> handleUniversalException(Exception e) {

        log.error("Exception occurred:  {}", e.getMessage(), e);
        return buildInternalServerErrorResponse("Internal error exception");
    }

}

