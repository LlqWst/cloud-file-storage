package dev.lqwd.cloudfilestorage.security.json_auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.lqwd.cloudfilestorage.dto.AuthRequestDTO;
import dev.lqwd.cloudfilestorage.exception.BadRequestException;
import dev.lqwd.cloudfilestorage.utils.Validator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
public class JsonAuthenticationFilter extends OncePerRequestFilter {

    private static final String POST = "POST";
    private static final String SIGN_IN_URL = "/api/auth/sign-in";
    private static final String FAILED_TO_PARSE_REQUEST_MESSAGE = "Failed to parse authentication request";
    private static final String METHOD_IS_NOT_ALLOWED_MESSAGE = "Method is not allowed";

    private final ObjectMapper objectMapper;
    private final AuthenticationManager authenticationManager;
    private final JsonAuthenticationSuccessHandler successHandler;
    private final JsonAuthenticationFailureHandler failureHandler;
    private final Validator validator;

    private final RequestMatcher LoginMatcher = PathPatternRequestMatcher
            .withDefaults()
            .matcher(SIGN_IN_URL);

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !LoginMatcher.matches(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws  IOException {

        try {
            if(!POST.equalsIgnoreCase(request.getMethod())){
                throw new BadRequestException(METHOD_IS_NOT_ALLOWED_MESSAGE);
            }
            Authentication authentication = attemptAuthentication(request);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            successHandler.onAuthenticationSuccess(request, response, authentication);

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            failureHandler.onException(request, response, e);
        }
    }

    private Authentication attemptAuthentication(HttpServletRequest request)
            throws AuthenticationException, BadRequestException {

        AuthRequestDTO authRequest = getAuthRequest(request);

        String username = authRequest.username();
        String password = authRequest.password();
        validator.validateCredentials(username, password);

        UsernamePasswordAuthenticationToken authToken =
                UsernamePasswordAuthenticationToken.unauthenticated(username, password);
        return authenticationManager.authenticate(authToken);
    }

    private AuthRequestDTO getAuthRequest(HttpServletRequest request) {
        try {
            return objectMapper.readValue(request.getInputStream(), AuthRequestDTO.class);
        } catch (IOException e) {
            throw new BadRequestException(FAILED_TO_PARSE_REQUEST_MESSAGE, e);
        }
    }
}
