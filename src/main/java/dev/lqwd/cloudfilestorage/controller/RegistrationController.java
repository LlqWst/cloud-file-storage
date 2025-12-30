package dev.lqwd.cloudfilestorage.controller;

import dev.lqwd.cloudfilestorage.dto.RegistrationRequestDto;
import dev.lqwd.cloudfilestorage.dto.UserResponseDto;
import dev.lqwd.cloudfilestorage.entity.User;
import dev.lqwd.cloudfilestorage.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class RegistrationController extends BaseController {

    private final AuthService authService;

    @PostMapping("/sign-up")
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody RegistrationRequestDto registrationRequest) {

        User user = authService.registrationAndLogin(registrationRequest);
        return buildCreatedResponse(new UserResponseDto(user.getUsername()), "id/" + user.getId());
    }
}
