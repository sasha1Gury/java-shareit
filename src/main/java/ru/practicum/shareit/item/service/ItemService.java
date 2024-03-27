package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto updateItem, long itemId, long userId);

    ItemDto findItemById(long itemId);

    List<ItemDto> findItemByUserId(long userId);

    List<ItemDto> searchItem(String text);
}
