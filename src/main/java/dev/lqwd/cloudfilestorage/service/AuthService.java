package dev.lqwd.cloudfilestorage.service;

import dev.lqwd.cloudfilestorage.dto.RegistrationRequestDto;
import dev.lqwd.cloudfilestorage.entity.Role;
import dev.lqwd.cloudfilestorage.entity.User;
import dev.lqwd.cloudfilestorage.entity.UserRole;
import dev.lqwd.cloudfilestorage.exception.AlreadyExistException;
import dev.lqwd.cloudfilestorage.repository.UserRepository;
import dev.lqwd.cloudfilestorage.service.storage.CreationService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class AuthService {

    private static final String ERROR_MESSAGE_USER_EXISTS = "User already exists";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CreationService creationService;
    private final AuthenticationManager authenticationManager;


    public User registrationAndLogin(RegistrationRequestDto registrationRequest, HttpSession session) {
        User user = registration(registrationRequest);
        creationService.createUserRootDir(user.getId());
        login(session, registrationRequest);
        return user;
    }

    private User registration(RegistrationRequestDto registrationRequest) {
        try {
            User user = User.builder()
                    .username(registrationRequest.username())
                    .password(passwordEncoder.encode(registrationRequest.password()))
                    .build();

            UserRole role = UserRole.builder()
                    .user(user)
                    .role(Role.ROLE_USER)
                    .build();

            user.getUserRoles().add(role);
            return userRepository.save(user);

        } catch (DataIntegrityViolationException e) {
            throw new AlreadyExistException(ERROR_MESSAGE_USER_EXISTS, e);
        }
    }

    private void login(HttpSession session, RegistrationRequestDto registrationRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        registrationRequest.username(),
                        registrationRequest.password()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
    }

}
