package ru.practicum.shareit.unit;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getByIdUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.findUserById(1));

        assertEquals("Объект User with id 1 not found не наден", exception.getMessage());
    }

    @Test
    void getByIdUserFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User(1, "name", "e@mail.ru")));

        UserDto user = userService.findUserById(1);

        assertEquals(1, user.getId());
        assertEquals("name", user.getName());
        assertEquals("e@mail.ru", user.getEmail());
    }

    @Test
    void updateIfUserNotExists() {
        final NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> userService.updateUser(new UserDto(1, "name", "e@mail.ru"), 1));

        assertEquals("No value present", exception.getMessage());
    }
}
