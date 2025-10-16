package dev.lqwd.cloudfilestorage.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.lqwd.cloudfilestorage.dto.AuthRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@AllArgsConstructor
public class JsonAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try {
            AuthRequestDTO authRequestDTO = objectMapper.readValue(request.getInputStream(), AuthRequestDTO.class);

            String username = authRequestDTO.username();
            String password = authRequestDTO.password();

            if (username == null) {
                username = "";
            }
            if (password == null) {
                password = "";
            }
            username = username.trim();

            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(username, password);

            setDetails(request, authRequest);

            return this.getAuthenticationManager().authenticate(authRequest);
        } catch (IOException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
    }
}
