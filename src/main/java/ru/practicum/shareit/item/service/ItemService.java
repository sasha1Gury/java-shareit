package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.Comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithTime;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto updateItem, long itemId, long userId);

    ItemDtoWithTime findItemById(long itemId, long userId);

    List<ItemDtoWithTime> findItemByUserId(long userId, Integer from, Integer size);

    List<ItemDto> searchItem(String text, Integer from, Integer size);

    CommentDto createComment(long itemId, long userId, CommentDto comment);
}
