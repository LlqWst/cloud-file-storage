package dev.lqwd.cloudfilestorage.security.json_auth;


import com.fasterxml.jackson.databind.ObjectMapper;
import dev.lqwd.cloudfilestorage.dto.ErrorResponseDTO;
import dev.lqwd.cloudfilestorage.exception.BadRequestException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@AllArgsConstructor
public class JsonAuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    public void onException(HttpServletRequest request,
                            HttpServletResponse response,
                            Exception ex) throws IOException {

        log.warn("Exception occurred:  {}", ex.getMessage(), ex);

        int httpStatus;
        if (ex instanceof AuthenticationException) {
            httpStatus = HttpStatus.UNAUTHORIZED.value();
        } else if (ex instanceof BadRequestException) {
            httpStatus = HttpStatus.BAD_REQUEST.value();
        } else {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR.value();
        }

        response.setStatus(httpStatus);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(ex.getMessage());
        objectMapper.writeValue(response.getWriter(), errorResponseDTO);
    }

}
