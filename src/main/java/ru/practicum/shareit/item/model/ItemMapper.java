package ru.practicum.shareit.item.model;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithTime;

import java.util.ArrayList;

public class ItemMapper {
    public static Item toEntity(ItemDto dto) {
        return new Item(dto.getId(),
                dto.getName(),
                dto.getDescription(),
                dto.getAvailable(),
                dto.getOwner(),
                null);
    }

    public static void updateEntity(Item dto, Item item) {
        if (dto.getName() != null) {
            item.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            item.setDescription(dto.getDescription());
        }
        if (dto.getAvailable() != null) {
            item.setAvailable(dto.getAvailable());
        }
    }

    public static ItemDto toDto(Item itemDto) {
        Long requestId = null;
        if (itemDto.getRequest() != null) {
            requestId = itemDto.getRequest().getId();
        }
        return new ItemDto(itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getOwner(),
                requestId);
    }

    public static ItemDtoWithTime toDtoWithTime(Item itemDto) {
        Long requestId = null;
        if (itemDto.getRequest() != null) {
            requestId = itemDto.getRequest().getId();
        }
        return new ItemDtoWithTime(itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getOwner(),
                requestId,
                new LastBooking(),
                new NextBooking(),
                new ArrayList<>());
    }
}
