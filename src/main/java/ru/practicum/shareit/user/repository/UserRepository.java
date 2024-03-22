package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;

@Repository
public class UserRepository {
    private long id = 1;
    private final Map<Long, User> users;

    public UserRepository() {
        users = new HashMap<>();
    }

    public User createUser(User user) {
        user.setId(id++);
        users.put(user.getId(), user);
        return user;
    }
}
