package dev.lqwd.cloudfilestorage.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) {

        if (authentication != null && authentication.isAuthenticated()) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } else {
            log.warn("Logout attempted without authentication");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
    }
}
