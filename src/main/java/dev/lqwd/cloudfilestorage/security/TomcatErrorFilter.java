package dev.lqwd.cloudfilestorage.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.lqwd.cloudfilestorage.dto.ErrorResponseDto;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Slf4j
@Component
public class TomcatErrorFilter implements Filter {

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

            new ObjectMapper()
                    .writeValue(httpResponse.getOutputStream(), new ErrorResponseDto("Internal error exception"));
        }
    }
}