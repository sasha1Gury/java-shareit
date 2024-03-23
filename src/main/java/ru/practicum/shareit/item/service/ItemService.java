package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;

public interface ItemService {
    Item createItem(Item item, long userId);

    Item updateItem(Map<String, Object> updates, long itemId, long userId);

    Item findItemById(long itemId);

    List<Item> findItemByUserId(long userId);

    List<Item> searchItem(String text);
}
