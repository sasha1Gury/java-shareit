package ru.practicum.shareit.item.model;

import ru.practicum.shareit.item.dto.ItemDto;

public class ItemMapper {
    public static Item toEntity(ItemDto dto) {
        return new Item(dto.getId(),
                dto.getName(),
                dto.getDescription(),
                dto.getAvailable(),
                dto.getOwner(),
                dto.getRequest());
    }

    public static ItemDto toDto(Item item) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                item.getRequest());
    }
}
