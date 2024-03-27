package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepositoryInMemory;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepositoryInMemory itemRepositoryInMemory;

    @Override
    public ItemDto createItem(ru.practicum.shareit.item.dto.ItemDto itemDto, long userId) {
        return itemRepositoryInMemory.createItem(itemDto, userId);
    }

    @Override
    public ItemDto updateItem(ItemDto updateItem, long itemId, long userId) {
        return itemRepositoryInMemory.updateItem(updateItem, itemId, userId);
    }

    @Override
    public ItemDto findItemById(long itemId) {
        return itemRepositoryInMemory.findItemDtoById(itemId);
    }

    @Override
    public List<ItemDto> findItemByUserId(long userId) {
        return itemRepositoryInMemory.findItemByUserId(userId);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        return itemRepositoryInMemory.searchItem(text);
    }
}
