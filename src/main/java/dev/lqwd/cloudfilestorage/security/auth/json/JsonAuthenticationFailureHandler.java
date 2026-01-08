package dev.lqwd.cloudfilestorage.security.auth.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.lqwd.cloudfilestorage.dto.ErrorResponseDto;
import dev.lqwd.cloudfilestorage.exception.BadRequestException;
import dev.lqwd.cloudfilestorage.exception.MethodNotAllowedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static dev.lqwd.cloudfilestorage.util.RepeatableErrorMessage.BAD_CREDENTIALS_ERROR_MESSAGE;
import static dev.lqwd.cloudfilestorage.util.RepeatableErrorMessage.INTERNAL_ERROR_MESSAGE;


@Component
@Slf4j
@AllArgsConstructor
public class JsonAuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    public void onException(HttpServletRequest request,
                            HttpServletResponse response,
                            Exception e) throws IOException {

        log.warn("Exception occurred while authenticate:  {}", e.getMessage(), e);

        switch (e) {
            case BadCredentialsException _ -> response(BAD_CREDENTIALS_ERROR_MESSAGE, HttpStatus.UNAUTHORIZED.value(), response);
            case AuthenticationException _ ->
                    response("Authentication failed", HttpStatus.UNAUTHORIZED.value(), response);
            case MethodNotAllowedException _ ->
                    response(e.getMessage(), HttpStatus.METHOD_NOT_ALLOWED.value(), response);
            case BadRequestException _ -> response(e.getMessage(), HttpStatus.BAD_REQUEST.value(), response);
            default -> response(INTERNAL_ERROR_MESSAGE, HttpStatus.BAD_REQUEST.value(), response);
        }
    }

    private void response(String message, int httpStatus, HttpServletResponse response) throws IOException {
        response.setStatus(httpStatus);
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        objectMapper.writeValue(response.getWriter(), new ErrorResponseDto(message));
    }

}
