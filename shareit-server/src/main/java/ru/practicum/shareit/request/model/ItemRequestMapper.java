package ru.practicum.shareit.request.model;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;

public class ItemRequestMapper {
    public static ItemRequest toEntity(ItemRequestDto dto) {
        return new ItemRequest(dto.getId(),
                dto.getDescription(),
                new User(),
                dto.getCreated());
    }

    public static ItemRequestDto toDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                new ArrayList<>());
    }
}
