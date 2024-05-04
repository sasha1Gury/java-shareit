package ru.practicum.shareit.unit.mapperTests;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserMapperTest {

    @Test
    public void testToEntity() {
        UserDto dto = UserDto.builder().build();
        dto.setId(1L);
        dto.setName("Test Name");
        dto.setEmail("test@example.com");

        User user = UserMapper.toEntity(dto);

        assertNotNull(user);
        assertEquals(dto.getId(), user.getId());
        assertEquals(dto.getName(), user.getName());
        assertEquals(dto.getEmail(), user.getEmail());
    }

    @Test
    public void testUpdateEntity() {
        User user = new User();
        user.setId(1L);
        user.setName("Old Name");
        user.setEmail("old@example.com");

        User dto = User.builder().build();
        dto.setName("New Name");
        dto.setEmail("new@example.com");

        UserMapper.updateEntity(dto, user);

        assertEquals(dto.getName(), user.getName());
        assertEquals(dto.getEmail(), user.getEmail());
    }

    @Test
    public void testToDto() {
        User user = new User();
        user.setId(1L);
        user.setName("Test Name");
        user.setEmail("test@example.com");

        UserDto dto = UserMapper.toDto(user);

        assertNotNull(dto);
        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getName(), dto.getName());
        assertEquals(user.getEmail(), dto.getEmail());
    }
}
