package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.exception.EmailExistException;
import ru.practicum.shareit.user.exception.NewUserException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepository {
    private long id = 1;
    private final Map<Long, User> users;
    private final List<String> emails;

    public UserRepository() {
        users = new HashMap<>();
        emails = new ArrayList<>();
    }

    public User createUser(User user) {
        if (users.containsValue(user)) {
            throw new NewUserException(user.getEmail());
        }
        user.setId(id++);
        emails.add(user.getEmail());
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(Map<String, Object> updates, long userId) {
        User user = findUserById(userId);
        updates.forEach((key, value) -> {
            switch (key) {
                case "name":
                    user.setName((String) value);
                    break;
                case "email":
                    if (emails.contains((String) value)) {
                        throw new EmailExistException((String) value);
                    } else user.setEmail((String) value);
                    break;
                default:
                    throw new UnsupportedOperationException("Поле '" + key + "' не может быть обновлено");
            }
        });
        users.put(userId, user);
        return user;
    }

    public User findUserById(long userId) {
        if (users.get(userId) == null) {
            throw  new NotFoundException(String.valueOf(userId));
        }
        return users.get(userId);
    }

    public void deleteUser(long userId) {
        User user = findUserById(userId);
        emails.remove(user.getEmail());
        users.remove(userId, user);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}
