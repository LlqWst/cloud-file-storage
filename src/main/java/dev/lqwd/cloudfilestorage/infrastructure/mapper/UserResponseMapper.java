package dev.lqwd.cloudfilestorage.infrastructure.mapper;

import dev.lqwd.cloudfilestorage.dto.UserResponseDto;
import dev.lqwd.cloudfilestorage.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserResponseMapper {

    @Mapping(source = "username", target = "username")
    UserResponseDto toUserResponseDto(User user);
    UserResponseDto toUserResponseDto(String username);
}
