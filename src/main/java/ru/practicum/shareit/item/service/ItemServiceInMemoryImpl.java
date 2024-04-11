package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.Comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithTime;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepositoryInMemory;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Qualifier("ItemServiceInMemoryImpl")
public class ItemServiceInMemoryImpl implements ItemService {
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
    public ItemDtoWithTime findItemById(long itemId, long userId) {
        return ItemMapper.toDtoWithTime(itemRepositoryInMemory.findItemDtoById(itemId));
    }

    @Override
    public List<ItemDtoWithTime> findItemByUserId(long userId) {
        return itemRepositoryInMemory.findItemByUserId(userId).stream()
                .map(ItemMapper::toDtoWithTime)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        return itemRepositoryInMemory.searchItem(text).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(long itemId, long userId, CommentDto comment) {
        return new CommentDto();
    }
}
