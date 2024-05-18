package ru.practicum.ewm.service.user;

import ru.practicum.ewm.model.user.dto.NewUserDto;
import ru.practicum.ewm.model.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getUsers(List<Long> ids, int from, int size);

    UserDto addUser(NewUserDto newUser);

    void deleteUser(Long userId);
}
