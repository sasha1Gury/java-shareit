package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.exception.NewItemException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.Map;

public class ItemRepository {
    private final Map<Long, Item> items;
    private long id = 1;

    public ItemRepository() {
        items = new HashMap<>();
    }

    public Item createItem(Item item) {
        if (items.containsValue(item)) {
            throw new NewItemException();
        }
        item.setId(id++);
        items.put(item.getId(), item);
        return item;
    }
}
