package org.nazar.mappers;

import org.mapstruct.Mapper;
import org.nazar.models.dto.UserDto;
import org.nazar.models.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    User toEntity(UserDto userDto);
}
