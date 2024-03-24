package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    User createUser(User user);

    User updateUser(Map<String, Object> updates, long userId);

    User findUserById(long userId);

    void deleteUser(long userId);

    List<User> getAllUsers();
}
