package dev.lqwd.cloudfilestorage.controller;

import dev.lqwd.cloudfilestorage.dto.RegistrationRequestDTO;
import dev.lqwd.cloudfilestorage.dto.UserResponseDTO;
import dev.lqwd.cloudfilestorage.entity.User;
import dev.lqwd.cloudfilestorage.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class RegistrationController {

    RegistrationService registrationService;

    @PostMapping("/sign-up")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody RegistrationRequestDTO registrationRequest){

        User user = registrationService.registration(registrationRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new UserResponseDTO(user.getUsername()));
    }

}
