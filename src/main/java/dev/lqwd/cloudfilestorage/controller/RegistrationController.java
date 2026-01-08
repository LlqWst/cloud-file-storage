package dev.lqwd.cloudfilestorage.controller;

import dev.lqwd.cloudfilestorage.controller.api.RegistrationApi;
import dev.lqwd.cloudfilestorage.dto.RegistrationRequestDto;
import dev.lqwd.cloudfilestorage.dto.UserResponseDto;
import dev.lqwd.cloudfilestorage.entity.User;
import dev.lqwd.cloudfilestorage.infrastructure.mapper.UserResponseMapper;
import dev.lqwd.cloudfilestorage.service.auth.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class RegistrationController extends BaseController implements RegistrationApi {

    private static final String USER_LOCATION_HEADER_PATTERN = "id/";
    private final RegistrationService registrationService;
    private final UserResponseMapper mapper;

    @Override
    public ResponseEntity<UserResponseDto> createUser(RegistrationRequestDto registrationRequest) {

        User user = registrationService.registrationAndLogin(registrationRequest);
        return buildCreatedResponse(mapper.toUserResponseDto(user), USER_LOCATION_HEADER_PATTERN + user.getId());
    }
}
