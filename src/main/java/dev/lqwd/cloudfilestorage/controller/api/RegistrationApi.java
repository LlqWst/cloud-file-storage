package dev.lqwd.cloudfilestorage.controller.api;

import dev.lqwd.cloudfilestorage.dto.RegistrationRequestDto;
import dev.lqwd.cloudfilestorage.dto.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping(value = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(
        name = "Registration controller",
        description = "Operations related to registration and auto login"
)
public interface RegistrationApi {

    @PostMapping("/sign-up")
    @Operation(
            summary = "Registration and auto login",
            description = "After successful registration and authorization, endpoint returns the user's username."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "registration and authorization completed"),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content()),
            @ApiResponse(responseCode = "409", description = "User already exists", content = @Content()),
            @ApiResponse(responseCode = "500", description = "Internal error exception", content = @Content())
    })
    ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody RegistrationRequestDto registrationRequest);
}