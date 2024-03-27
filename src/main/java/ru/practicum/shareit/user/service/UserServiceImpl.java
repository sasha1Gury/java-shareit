package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepositoryInMemory;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepositoryInMemory userRepositoryInMemory;

    @Override
    public UserDto createUser(UserDto user) {
        return userRepositoryInMemory.createUser(user);
    }

    @Override
    public UserDto updateUser(UserDto updates, long userId) {
        return userRepositoryInMemory.updateUser(updates, userId);
    }

    @Override
    public UserDto findUserById(long userId) {
        return userRepositoryInMemory.findUserDtoById(userId);
    }

    @Override
    public void deleteUser(long userId) {
        userRepositoryInMemory.deleteUser(userId);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepositoryInMemory.getAllUsers();
    }
}
