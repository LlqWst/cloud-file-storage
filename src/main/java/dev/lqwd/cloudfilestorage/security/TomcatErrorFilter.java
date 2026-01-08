package dev.lqwd.cloudfilestorage.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.lqwd.cloudfilestorage.dto.ErrorResponseDto;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static dev.lqwd.cloudfilestorage.util.RepeatableErrorMessage.INTERNAL_ERROR_MESSAGE;


@Slf4j
@Component
@AllArgsConstructor
public class TomcatErrorFilter implements Filter {

    private final ObjectMapper objectMapper;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            chain.doFilter(request, response);
        } catch (Throwable t) {
            log.error("Tomcat error caught: {}", t.getMessage(), t);


            httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
            httpResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

            objectMapper.writeValue(httpResponse.getOutputStream(), new ErrorResponseDto(INTERNAL_ERROR_MESSAGE));
        }
    }
}