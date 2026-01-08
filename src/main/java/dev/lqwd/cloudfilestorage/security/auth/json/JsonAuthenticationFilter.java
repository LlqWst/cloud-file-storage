package dev.lqwd.cloudfilestorage.security.auth.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.lqwd.cloudfilestorage.dto.AuthRequestDto;
import dev.lqwd.cloudfilestorage.exception.BadRequestException;
import dev.lqwd.cloudfilestorage.exception.MethodNotAllowedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
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

import static dev.lqwd.cloudfilestorage.util.RepeatableErrorMessage.BAD_CREDENTIALS_ERROR_MESSAGE;
import static dev.lqwd.cloudfilestorage.util.RepeatableErrorMessage.METHOD_NOT_ALLOWED_ERROR_MESSAGE;


@Component
@AllArgsConstructor
public class JsonAuthenticationFilter extends OncePerRequestFilter {

    private static final String POST = "POST";
    private static final String SIGN_IN_URL = "/api/auth/sign-in";

    private final ObjectMapper objectMapper;
    private final Validator validator;
    private final AuthenticationManager authenticationManager;
    private final JsonAuthenticationSuccessHandler successHandler;
    private final JsonAuthenticationFailureHandler failureHandler;
    private final RequestMatcher LoginMatcher = PathPatternRequestMatcher
            .withDefaults()
            .matcher(SIGN_IN_URL);

    @Override
    protected boolean shouldNotFilter(@NotNull HttpServletRequest request) {
        return !LoginMatcher.matches(request);
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws IOException {

        try {
            if (!POST.equalsIgnoreCase(request.getMethod())) {
                throw new MethodNotAllowedException(METHOD_NOT_ALLOWED_ERROR_MESSAGE + POST);
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

        AuthRequestDto authRequest = getAuthRequest(request);
        if (!validator.validate(authRequest).isEmpty()) {
            throw new BadRequestException(BAD_CREDENTIALS_ERROR_MESSAGE);
        }
        UsernamePasswordAuthenticationToken authToken =
                UsernamePasswordAuthenticationToken.unauthenticated(authRequest.username(), authRequest.password());
        return authenticationManager.authenticate(authToken);
    }

    private AuthRequestDto getAuthRequest(HttpServletRequest request) {
        try {
            return objectMapper.readValue(request.getInputStream(), AuthRequestDto.class);
        } catch (IOException e) {
            logger.warn("Failed to parse authentication request");
            throw new BadRequestException(BAD_CREDENTIALS_ERROR_MESSAGE, e);
        }
    }
}
