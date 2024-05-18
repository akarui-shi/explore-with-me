package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.model.user.dto.NewUserDto;
import ru.practicum.ewm.model.user.dto.UserDto;
import ru.practicum.ewm.model.user.dto.UserShortDto;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    User fromDto(UserDto userDto);

    User fromDto(NewUserDto newUserDto);

    UserShortDto toShortDto(User user);

}
