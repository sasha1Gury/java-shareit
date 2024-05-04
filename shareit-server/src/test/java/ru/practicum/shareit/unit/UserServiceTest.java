package ru.practicum.shareit.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.EmailExistException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepository mockUserRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getByIdUserNotFound() {
        when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.findUserById(1));

        assertEquals("Объект User with id 1 not found не наден", exception.getMessage());
    }

    @Test
    void getByIdUserFound() {
        when(mockUserRepository.findById(anyLong()))
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

    @Test
    void createUserNormalWay() {
        when(mockUserRepository.save(Mockito.any(User.class)))
                .thenReturn(new User(1, "name", "e@mail.ru"));

        UserDto user = userService.createUser(new UserDto(1, "name", "e@mail.ru"));

        Assertions.assertEquals(1, user.getId());
        Assertions.assertEquals("name", user.getName());
        Assertions.assertEquals("e@mail.ru", user.getEmail());
    }

    @Test
    void createUserDuplicatedEmail() {
        when(mockUserRepository.save(Mockito.any(User.class)))
                .thenThrow(new EmailExistException("email"));

        final EmailExistException exception = Assertions.assertThrows(
                EmailExistException.class,
                () -> userService.createUser(new UserDto(1, "name", "e@mail.ru")));

        Assertions.assertEquals("Пользователь с таким email уже существует", exception.getMessage());
    }

    @Test
    void updateUserNormalWay() {
        when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(1, "name", "e@mail.ru")));

        when(mockUserRepository.save(Mockito.any(User.class)))
                .thenReturn(new User(1, "name", "e@mail.ru"));

        UserDto user = userService.updateUser(new UserDto(1, "name", "e@mail.ru"), 1);

        Assertions.assertEquals(1, user.getId());
        Assertions.assertEquals("name", user.getName());
        Assertions.assertEquals("e@mail.ru", user.getEmail());
    }

    @Test
    void getAllTest() {
        when(mockUserRepository.findAll())
                .thenReturn(List.of(new User(1, "name", "e@mail.ru")));

        List<UserDto> userList = userService.getAllUsers();

        assertEquals(1, userList.size());
        assertEquals(1, userList.get(0).getId());
    }

    @Test
    void deleteTest() {
        userService.deleteUser(1);

        verify(mockUserRepository).deleteById(1L);
    }
}
