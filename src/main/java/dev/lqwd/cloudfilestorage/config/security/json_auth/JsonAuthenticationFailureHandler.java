package dev.lqwd.cloudfilestorage.config.security.json_auth;


import com.fasterxml.jackson.databind.ObjectMapper;
import dev.lqwd.cloudfilestorage.dto.ErrorResponseDTO;
import dev.lqwd.cloudfilestorage.exception.BadRequestException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@AllArgsConstructor
public class JsonAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        onException(request, response, exception, HttpStatus.UNAUTHORIZED.value());
    }

    public void onBadRequest(HttpServletRequest request,
                             HttpServletResponse response,
                             BadRequestException exception) throws IOException {

        onException(request, response, exception, HttpStatus.BAD_REQUEST.value());
    }

    public void onInternalException(HttpServletRequest request,
                                    HttpServletResponse response,
                                    Exception exception) throws IOException {

        onException(request, response, exception, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private void onException(HttpServletRequest request,
                             HttpServletResponse response,
                             Exception exception,
                             int httpStatus) throws IOException {

        log.warn("Exception occurred:  {}", exception.getMessage(), exception);

        response.setStatus(httpStatus);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(exception.getMessage());
        objectMapper.writeValue(response.getWriter(), errorResponseDTO);
    }

}
