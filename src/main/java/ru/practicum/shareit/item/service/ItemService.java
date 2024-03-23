package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.Map;

public interface ItemService {
    Item createItem(Item item, long userId);

    Item updateItem(Map<String, Object> updates, long itemId, long userId);
}
