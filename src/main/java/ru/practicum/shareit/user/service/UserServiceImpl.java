package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Qualifier("UserServiceImpl")
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toEntity(userDto);
        return UserMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUser(UserDto updates, long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        User dto = UserMapper.toEntity(updates);
        UserMapper.updateEntity(dto, user);
        return UserMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserDto findUserById(long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new NotFoundException(String.format("User with id %s not found", userId));
        }
        return UserMapper.toDto(user);
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }
}
