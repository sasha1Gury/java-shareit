package ru.practicum.shareit.request.model;

import ru.practicum.shareit.request.dto.ItemRequestDto;

public class ItemRequestMapper {
    public static ItemRequest toEntity(ItemRequestDto dto) {
        return new ItemRequest(dto.getId(),
                dto.getDescription(),
                dto.getOwner());
    }

    public static ItemRequestDto toDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getOwner());
    }
}
