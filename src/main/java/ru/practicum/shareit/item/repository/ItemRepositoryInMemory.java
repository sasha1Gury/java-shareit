package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
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

    public Item createItem(Item item, long userId) {
        if (item.getAvailable() == null) {
            throw new AvailableNotInitInItem();
        }
        User user = userRepositoryInMemory.findUserById(userId);
        if (user == null) {
            throw new NotFoundException(String.valueOf(userId));
        }
        item.setOwner(user);
        if (items.containsValue(item)) {
            throw new NewItemException();
        }
        item.setId(id++);
        items.put(item.getId(), item);
        return item;
    }

    public Item updateItem(Item updates, long itemId, long userId) {
        Item item = findItemById(itemId);

        User user = userRepositoryInMemory.findUserById(userId);
        if (!item.getOwner().equals(user)) {
            throw new OwnerException();
        }

        ItemMapper.updateEntity(updates, item);
        items.put(item.getId(), item);
        return item;
    }

    public Item findItemDtoById(long itemId) {
        if (items.get(itemId) == null) {
            throw new NotFoundException(String.valueOf(itemId));
        }
        return items.get(itemId);
    }

    public Item findItemById(long itemId) {
        if (items.get(itemId) == null) {
            throw new NotFoundException(String.valueOf(itemId));
        }
        return items.get(itemId);
    }

    public List<Item> findItemByUserId(long userId) {
        User owner = userRepositoryInMemory.findUserById(userId);
        return items.values().stream()
                .filter(item -> item.getOwner().equals(owner))
                .collect(Collectors.toList());
    }

    public List<Item> getAllItems() {
        return new ArrayList<>(items.values());
    }

    public List<Item> searchItem(String searchText) {
        if (searchText.isBlank()) {
            return new ArrayList<>();
        }
        return getAllItems().stream()
                .filter(item -> item.getName().toLowerCase().contains(searchText)
                        || item.getDescription().toLowerCase().contains(searchText))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }
}
