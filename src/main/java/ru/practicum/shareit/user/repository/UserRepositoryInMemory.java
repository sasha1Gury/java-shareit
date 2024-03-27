package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.EmailExistException;
import ru.practicum.shareit.user.exception.NewUserException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryInMemory {
    private long id = 1;
    private final Map<Long, User> users;
    private final List<String> emails;

    public UserRepositoryInMemory() {
        users = new HashMap<>();
        emails = new ArrayList<>();
    }

    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toEntity(userDto);
        if (users.containsValue(user)) {
            throw new NewUserException(user.getEmail());
        }
        user.setId(id++);
        emails.add(user.getEmail());
        users.put(user.getId(), user);
        return UserMapper.toDto(user);
    }

    public UserDto updateUser(UserDto updates, long userId) {
        User user = findUserById(userId);

        if (emails.contains(updates.getEmail())) {
            throw new EmailExistException(updates.getEmail());
        }

        UserMapper.updateEntity(updates, user);
        users.put(user.getId(), user);
        return UserMapper.toDto(user);
    }

    public User findUserById(long userId) {
        if (users.get(userId) == null) {
            throw  new NotFoundException(String.valueOf(userId));
        }
        return users.get(userId);
    }

    public UserDto findUserDtoById(long userId) {
        if (users.get(userId) == null) {
            throw  new NotFoundException(String.valueOf(userId));
        }
        return UserMapper.toDto(users.get(userId));
    }

    public void deleteUser(long userId) {
        User user = findUserById(userId);
        emails.remove(user.getEmail());
        users.remove(userId, user);
    }

    public List<UserDto> getAllUsers() {
        return users.values().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }
}
