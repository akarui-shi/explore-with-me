package ru.practicum.ewm.service.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.model.user.dto.NewUserDto;
import ru.practicum.ewm.model.user.dto.UserDto;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.service.user.specification.UserSpecification.idIn;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        Specification<User> idIn = idIn(ids);
        List<User> users = userRepository.findAll(idIn, pageRequest).getContent();
        log.info("Requesting users with ids = '{}', from = '{}', size = '{}'.", ids, from, size);
        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto addUser(NewUserDto newUser) {
        User savedUser = userRepository.save(userMapper.fromDto(newUser));
        log.info("Saved user: {}", savedUser);
        return userMapper.toDto(savedUser);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new RuntimeException("User not found"));

        log.info("Deleting user: {}", user);
        userRepository.delete(user);
    }
}
