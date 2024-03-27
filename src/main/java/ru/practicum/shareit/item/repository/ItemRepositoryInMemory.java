package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.AvailableNotInitInItem;
import ru.practicum.shareit.item.exception.NewItemException;
import ru.practicum.shareit.item.exception.OwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryInMemory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryInMemory {
    private final Map<Long, Item> items;
    private final UserRepositoryInMemory userRepositoryInMemory;
    private long id = 1;

    public ItemDto createItem(ItemDto itemDto, long userId) {
        Item item = ItemMapper.toEntity(itemDto);
        if (item.getAvailable() == null) {
            throw new AvailableNotInitInItem();
        }
        User user = userRepositoryInMemory.findUserById(userId);
        if (user == null) {
            throw new NotFoundException(String.valueOf(userId));
        }
        item.setOwner(userId);
        if (items.containsValue(item)) {
            throw new NewItemException();
        }
        item.setId(id++);
        items.put(item.getId(), item);
        return ItemMapper.toDto(item);
    }

    public ItemDto updateItem(ItemDto updates, long itemId, long userId) {
        Item item = findItemById(itemId);

        User user = userRepositoryInMemory.findUserById(userId);
        if (item.getOwner() != user.getId()) {
            throw new OwnerException();
        }

        ItemMapper.updateEntity(updates, item);
        items.put(item.getId(), item);
        return ItemMapper.toDto(item);
    }

    public ItemDto findItemDtoById(long itemId) {
        if (items.get(itemId) == null) {
            throw new NotFoundException(String.valueOf(itemId));
        }
        return ItemMapper.toDto(items.get(itemId));
    }

    public Item findItemById(long itemId) {
        if (items.get(itemId) == null) {
            throw new NotFoundException(String.valueOf(itemId));
        }
        return items.get(itemId);
    }

    public List<ItemDto> findItemByUserId(long userId) {
        return items.values().stream()
                .map(ItemMapper::toDto)
                .filter(item -> item.getOwner() == userId)
                .collect(Collectors.toList());
    }

    public List<Item> getAllItems() {
        return new ArrayList<>(items.values());
    }

    public List<ItemDto> searchItem(String searchText) {
        if (searchText.isBlank()) {
            return new ArrayList<>();
        }
        return getAllItems().stream()
                .map(ItemMapper::toDto)
                .filter(item -> item.getName().toLowerCase().contains(searchText)
                        || item.getDescription().toLowerCase().contains(searchText))
                .filter(ItemDto::getAvailable)
                .collect(Collectors.toList());
    }
}
