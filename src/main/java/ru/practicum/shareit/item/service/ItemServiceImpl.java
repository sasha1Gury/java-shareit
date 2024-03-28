package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepositoryInMemory;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepositoryInMemory itemRepositoryInMemory;

    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        Item item = ItemMapper.toEntity(itemDto);
        return ItemMapper.toDto(itemRepositoryInMemory.createItem(item, userId));
    }

    @Override
    public ItemDto updateItem(ItemDto updateItemDto, long itemId, long userId) {
        Item updateItem = ItemMapper.toEntity(updateItemDto);
        return ItemMapper.toDto(itemRepositoryInMemory.updateItem(updateItem, itemId, userId));
    }

    @Override
    public ItemDto findItemById(long itemId) {
        return ItemMapper.toDto(itemRepositoryInMemory.findItemDtoById(itemId));
    }

    @Override
    public List<ItemDto> findItemByUserId(long userId) {
        return itemRepositoryInMemory.findItemByUserId(userId).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        return itemRepositoryInMemory.searchItem(text).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }
}
