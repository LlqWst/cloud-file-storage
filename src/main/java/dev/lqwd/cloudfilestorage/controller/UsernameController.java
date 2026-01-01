package dev.lqwd.cloudfilestorage.controller;

import dev.lqwd.cloudfilestorage.controller.api.UsernameApi;
import dev.lqwd.cloudfilestorage.dto.UserResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class UsernameController extends BaseController implements UsernameApi {

    @Override
    public ResponseEntity<UserResponseDto> getUsername(UserDetails userDetails) {

        return buildOkResponse(new UserResponseDto(userDetails.getUsername()));
    }
}
