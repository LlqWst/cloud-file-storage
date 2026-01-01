package dev.lqwd.cloudfilestorage.controller;

import dev.lqwd.cloudfilestorage.controller.api.RegistrationApi;
import dev.lqwd.cloudfilestorage.dto.RegistrationRequestDto;
import dev.lqwd.cloudfilestorage.dto.UserResponseDto;
import dev.lqwd.cloudfilestorage.entity.User;
import dev.lqwd.cloudfilestorage.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class RegistrationController extends BaseController implements RegistrationApi {

    private final AuthService authService;

    @Override
    public ResponseEntity<UserResponseDto> createUser(RegistrationRequestDto registrationRequest) {

        User user = authService.registrationAndLogin(registrationRequest);
        return buildCreatedResponse(new UserResponseDto(user.getUsername()), "id/" + user.getId());
    }
}
