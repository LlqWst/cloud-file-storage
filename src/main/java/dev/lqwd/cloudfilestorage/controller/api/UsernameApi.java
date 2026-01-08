package dev.lqwd.cloudfilestorage.controller.api;

import dev.lqwd.cloudfilestorage.dto.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Username controller", description = "Return the username")
public interface UsernameApi {

    @GetMapping("/user/me")
    @Operation(
            summary = "Returns the authorized user's username",
            description = "Returns the authorized user's username"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authorized"),
            @ApiResponse(responseCode = "401", description = "User unauthorized", content = @Content()),
            @ApiResponse(responseCode = "500", description = "Internal error", content = @Content())
    })
    ResponseEntity<UserResponseDto> getUsername(@AuthenticationPrincipal UserDetails userDetails);
}