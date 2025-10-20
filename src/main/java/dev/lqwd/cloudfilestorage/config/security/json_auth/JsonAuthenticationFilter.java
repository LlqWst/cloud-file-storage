package dev.lqwd.cloudfilestorage.config.security.json_auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.lqwd.cloudfilestorage.dto.AuthRequestDTO;
import dev.lqwd.cloudfilestorage.exception.BadRequestException;
import dev.lqwd.cloudfilestorage.utils.Validator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
public class JsonAuthenticationFilter extends OncePerRequestFilter {

    private static final String POST = "POST";
    private static final String SIGN_IN_URL = "/api/auth/sign-in";
    private static final String FAILED_TO_PARSE_REQUEST = "Failed to parse authentication request";

    private final ObjectMapper objectMapper;
    private final AuthenticationManager authenticationManager;
    private final JsonAuthenticationSuccessHandler successHandler;
    private final JsonAuthenticationFailureHandler failureHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (isNotLoginRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Authentication authentication = attemptAuthentication(request);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            successHandler.onAuthenticationSuccess(request, response, authentication);

        } catch (AuthenticationException e) {
            failureHandler.onAuthenticationFailure(request, response, e);
        } catch (BadRequestException e) {
            failureHandler.onBadRequest(request, response, e);
        } catch (Exception e) {
            failureHandler.onInternalException(request, response, e);
        }
    }

    private Authentication attemptAuthentication(HttpServletRequest request)
            throws AuthenticationException, BadRequestException {

        AuthRequestDTO authRequest = getAuthRequest(request);

        String username = authRequest.username();
        String password = authRequest.password();
        Validator.validateCredentials(username, password);

        UsernamePasswordAuthenticationToken authToken =
                UsernamePasswordAuthenticationToken.unauthenticated(username, password);
        return authenticationManager.authenticate(authToken);
    }

    private static boolean isNotLoginRequest(HttpServletRequest request) {
        return !SIGN_IN_URL.equals(request.getServletPath()) ||
               !POST.equals(request.getMethod());
    }

    private AuthRequestDTO getAuthRequest(HttpServletRequest request) {
        try {
            return objectMapper.readValue(
                    request.getInputStream(), AuthRequestDTO.class);

        } catch (IOException e) {
            throw new BadRequestException(FAILED_TO_PARSE_REQUEST, e);
        }
    }
}
