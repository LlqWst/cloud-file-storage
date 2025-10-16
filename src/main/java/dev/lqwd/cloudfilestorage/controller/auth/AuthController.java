package dev.lqwd.cloudfilestorage.controller.auth;

import dev.lqwd.cloudfilestorage.dto.AuthRequestDTO;
import dev.lqwd.cloudfilestorage.dto.UserResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/sign-in")
    public String HiFromSignIn(){

        return "hi";
    }


    @PostMapping("/sign-in")
    public ResponseEntity<UserResponseDTO> validateUserCredentials(@Valid @RequestBody AuthRequestDTO authRequestDTO){

        return ResponseEntity
                .ok()
                .body(new UserResponseDTO(authRequestDTO.username()));
    }

}
