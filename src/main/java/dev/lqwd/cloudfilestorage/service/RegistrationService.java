package dev.lqwd.cloudfilestorage.service;

import dev.lqwd.cloudfilestorage.dto.RegistrationRequestDTO;
import dev.lqwd.cloudfilestorage.entity.Role;
import dev.lqwd.cloudfilestorage.entity.User;
import dev.lqwd.cloudfilestorage.entity.UserRole;
import dev.lqwd.cloudfilestorage.exception.AlreadyExistException;
import dev.lqwd.cloudfilestorage.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class RegistrationService {

    private static final String ERROR_MESSAGE_USER_EXISTS = "User already exists";

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    public User registration(RegistrationRequestDTO registrationRequest) {
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

}
