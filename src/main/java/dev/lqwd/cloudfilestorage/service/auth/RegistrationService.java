package dev.lqwd.cloudfilestorage.service.auth;

import dev.lqwd.cloudfilestorage.dto.RegistrationRequestDto;
import dev.lqwd.cloudfilestorage.entity.User;
import dev.lqwd.cloudfilestorage.service.storage.operations.CreationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@AllArgsConstructor
public class RegistrationService {

    private final UserService userService;
    private final AuthService authService;
    private final CreationService creationService;

    public User registrationAndLogin(RegistrationRequestDto request) {
        User user = RegistrationOperationExecutor.executeWithLog(
                "User registration",
                () -> userService.create(request)
        );

        RegistrationOperationExecutor.executeWithCompensation(
                "Create user directory",
                () -> creationService.createUserRootDir(user.getId()),
                () -> userService.delete(user)
        );

        RegistrationOperationExecutor.executeSuppressingErrors(
                "User login",
                () -> authService.login(request.username(), request.password())
        );

        return user;
    }

}
