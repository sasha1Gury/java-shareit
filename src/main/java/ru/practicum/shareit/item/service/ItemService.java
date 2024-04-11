package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithTime;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto updateItem, long itemId, long userId);

    ItemDtoWithTime findItemById(long itemId, long userId);

    List<ItemDtoWithTime> findItemByUserId(long userId);

    List<ItemDto> searchItem(String text);
}
