package dev.lqwd.cloudfilestorage.controller;

import dev.lqwd.cloudfilestorage.dto.RegistrationRequestDTO;
import dev.lqwd.cloudfilestorage.dto.UserResponseDTO;
import dev.lqwd.cloudfilestorage.entity.User;
import dev.lqwd.cloudfilestorage.service.MinioService;
import dev.lqwd.cloudfilestorage.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class RegistrationController {

    RegistrationService registrationService;
    MinioService minioService;

    @PostMapping("/sign-up")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody RegistrationRequestDTO registrationRequest){

        User user = registrationService.registration(registrationRequest);
        long id = user.getId();
        minioService.createUserRootDir(id);

        return ResponseEntity
                .created(URI.create("id/" + id))
                .body(new UserResponseDTO(user.getUsername()));
    }
}
