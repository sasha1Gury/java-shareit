package ru.practicum.shareit.user.model;

import ru.practicum.shareit.user.dto.UserDto;

public class UserMapper {
    public static User toEntity(UserDto dto) {
        return new User(dto.getId(),
                dto.getName(),
                dto.getEmail());
    }

    public static void updateEntity(User dto, User user) {
        if (dto.getName() != null) {
            user.setName(dto.getName());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
    }

    public static UserDto toDto(User user) {
        return new UserDto(user.getId(),
                user.getName(),
                user.getEmail());
    }
}
