package ru.practicum.shareit.unit;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private long userId = 1;

    @Test
    public void updateUserTest() {
        // Set up a mock user
        User user = new User();
        user.setId(userId);
        user.setEmail("testEmail");
        user.setName("name");

        // Set up an updated user
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setName("updateName");
        updateUser.setEmail("");

        // Update the user entity
        UserMapper.updateEntity(user, updateUser);

        // Set up mock repository responses
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(updateUser);

        // Call the method under test
        UserDto result = userService.updateUser(UserMapper.toDto(updateUser), userId);

        // Assertions
        assertEquals(updateUser.getName(), result.getName());
    }
}
