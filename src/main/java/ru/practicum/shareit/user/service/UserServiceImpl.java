package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryInMemory;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepositoryInMemory userRepositoryInMemory;

    @Override
    public User createUser(User user) {
        return userRepositoryInMemory.createUser(user);
    }

    @Override
    public User updateUser(Map<String, Object> updates, long userId) {
        return userRepositoryInMemory.updateUser(updates, userId);
    }

    @Override
    public User findUserById(long userId) {
        return userRepositoryInMemory.findUserById(userId);
    }

    @Override
    public void deleteUser(long userId) {
        userRepositoryInMemory.deleteUser(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepositoryInMemory.getAllUsers();
    }
}
