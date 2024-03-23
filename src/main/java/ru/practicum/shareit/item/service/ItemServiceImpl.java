package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    @Override
    public Item createItem(Item item, long userId) {
        return itemRepository.createItem(item, userId);
    }

    @Override
    public Item updateItem(Map<String, Object> updates, long itemId, long userId) {
        return itemRepository.updateItem(updates, itemId, userId);
    }
}
