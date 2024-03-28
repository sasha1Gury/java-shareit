package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.repository.UserRepositoryInMemory;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepositoryInMemory userRepositoryInMemory;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toEntity(userDto);
        return UserMapper.toDto(userRepositoryInMemory.createUser(user));
    }

    @Override
    public UserDto updateUser(UserDto updatesDto, long userId) {
        User updates = UserMapper.toEntity(updatesDto);
        return UserMapper.toDto(userRepositoryInMemory.updateUser(updates, userId));
    }

    @Override
    public UserDto findUserById(long userId) {
        return UserMapper.toDto(userRepositoryInMemory.findUserById(userId));
    }

    @Override
    public void deleteUser(long userId) {
        userRepositoryInMemory.deleteUser(userId);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepositoryInMemory.getAllUsers().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }
}
