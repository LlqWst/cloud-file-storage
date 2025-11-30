package dev.lqwd.cloudfilestorage.controller;

import dev.lqwd.cloudfilestorage.dto.RegistrationRequestDTO;
import dev.lqwd.cloudfilestorage.dto.UserResponseDTO;
import dev.lqwd.cloudfilestorage.entity.User;
import dev.lqwd.cloudfilestorage.service.MinioService;
import dev.lqwd.cloudfilestorage.service.RegistrationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;
    private final MinioService minioService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/sign-up")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody RegistrationRequestDTO registrationRequest,
                                                      HttpServletRequest request){

        User user = registrationService.registration(registrationRequest);
        long id = user.getId();
        minioService.createUserRootDir(id);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        registrationRequest.username(),
                        registrationRequest.password()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        HttpSession session = request.getSession();
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        return ResponseEntity
                .created(URI.create("id/" + id))
                .body(new UserResponseDTO(user.getUsername()));
    }
}
