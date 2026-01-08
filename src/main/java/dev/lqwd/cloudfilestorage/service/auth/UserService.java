package dev.lqwd.cloudfilestorage.service.auth;

import dev.lqwd.cloudfilestorage.dto.RegistrationRequestDto;
import dev.lqwd.cloudfilestorage.entity.Role;
import dev.lqwd.cloudfilestorage.entity.User;
import dev.lqwd.cloudfilestorage.entity.UserRole;
import dev.lqwd.cloudfilestorage.exception.AlreadyExistException;
import dev.lqwd.cloudfilestorage.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static dev.lqwd.cloudfilestorage.util.RepeatableErrorMessage.USER_ALREADY_EXISTS_ERROR_MESSAGE;


@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User create(RegistrationRequestDto registrationRequest) {
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
            throw new AlreadyExistException(USER_ALREADY_EXISTS_ERROR_MESSAGE, e);
        }
    }

    public void delete(User user){
        userRepository.delete(user);
    }

}
