package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User createUser(User user) {
        return userRepository.createUser(user);
    }

    public User updateUser(Map<String, Object> updates, long userId) {
        return userRepository.updateUser(updates, userId);
    }

    public User findUserById(long userId) {
        return userRepository.findUserById(userId);
    }

    public void deleteUser(long userId) {
        userRepository.deleteUser(userId);
    }

    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }
}
