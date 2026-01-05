package dev.lqwd.cloudfilestorage.security.json_auth;


import com.fasterxml.jackson.databind.ObjectMapper;
import dev.lqwd.cloudfilestorage.dto.ErrorResponseDto;
import dev.lqwd.cloudfilestorage.exception.BadRequestException;
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
            case BadCredentialsException _ ->
                    response("Bad credentials", HttpStatus.UNAUTHORIZED.value(), response);
            case AuthenticationException _ ->
                    response("Authentication failed", HttpStatus.UNAUTHORIZED.value(), response);
            case BadRequestException _ -> response(e.getMessage(), HttpStatus.BAD_REQUEST.value(), response);
            default -> response("Internal error", HttpStatus.BAD_REQUEST.value(), response);
        }

    }

    private void response(String message, int httpStatus, HttpServletResponse response) throws IOException {
        response.setStatus(httpStatus);
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        objectMapper.writeValue(response.getWriter(), new ErrorResponseDto(message));
    }

}
