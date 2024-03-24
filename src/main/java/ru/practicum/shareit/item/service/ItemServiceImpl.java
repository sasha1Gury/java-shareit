package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepositoryInMemory;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepositoryInMemory itemRepositoryInMemory;

    @Override
    public Item createItem(Item item, long userId) {
        return itemRepositoryInMemory.createItem(item, userId);
    }

    @Override
    public Item updateItem(Map<String, Object> updates, long itemId, long userId) {
        return itemRepositoryInMemory.updateItem(updates, itemId, userId);
    }

    @Override
    public Item findItemById(long itemId) {
        return itemRepositoryInMemory.findItemById(itemId);
    }

    @Override
    public List<Item> findItemByUserId(long userId) {
        return itemRepositoryInMemory.findItemByUserId(userId);
    }

    @Override
    public List<Item> searchItem(String text) {
        return itemRepositoryInMemory.searchItem(text);
    }
}
