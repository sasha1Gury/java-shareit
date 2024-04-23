package ru.practicum.shareit.unit;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void updateUserTest() {
        User user = new User();
        user.setId(1);
        user.setEmail("testEmail");
        user.setName("name");

        User updateUser = new User();
        updateUser.setId(1);
        updateUser.setName("updateName");
        updateUser.setEmail("");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        UserDto result = userService.updateUser(UserMapper.toDto(updateUser), user.getId());

        assertEquals(updateUser.getName(), result.getName());
    }
}
