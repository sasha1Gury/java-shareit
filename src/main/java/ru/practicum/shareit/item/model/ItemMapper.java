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

    public static void updateEntity(ItemDto dto, Item entity) {
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        if (dto.getAvailable() != null) {
            entity.setAvailable(dto.getAvailable());
        }
    }

    public static ItemDto toDto(Item itemDto) {
        return new ItemDto(itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getOwner(),
                itemDto.getRequest());
    }
}
