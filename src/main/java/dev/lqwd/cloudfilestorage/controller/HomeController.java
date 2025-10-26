package dev.lqwd.cloudfilestorage.controller;


import dev.lqwd.cloudfilestorage.dto.UserResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class HomeController {


    @GetMapping("/user/me")
    public ResponseEntity<UserResponseDTO> getUser(@AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity
                .ok()
                .body(new UserResponseDTO(userDetails.getUsername()));
    }
}
