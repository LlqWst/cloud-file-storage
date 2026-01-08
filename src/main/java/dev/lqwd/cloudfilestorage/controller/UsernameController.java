package dev.lqwd.cloudfilestorage.controller;

import dev.lqwd.cloudfilestorage.controller.api.UsernameApi;
import dev.lqwd.cloudfilestorage.dto.UserResponseDto;
import dev.lqwd.cloudfilestorage.infrastructure.mapper.UserResponseMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor
public class UsernameController extends BaseController implements UsernameApi {

    UserResponseMapper mapper;

    @Override
    public ResponseEntity<UserResponseDto> getUsername(UserDetails userDetails) {

        return buildOkResponse(mapper.toUserResponseDto(userDetails.getUsername()));
    }
}
