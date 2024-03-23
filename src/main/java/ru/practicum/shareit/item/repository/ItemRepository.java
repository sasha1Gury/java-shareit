package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.exception.AvailableNotInitInItem;
import ru.practicum.shareit.item.exception.NewItemException;
import ru.practicum.shareit.item.exception.OwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
    private final Map<Long, Item> items;
    private final UserRepository userRepository;
    private long id = 1;

    public Item createItem(Item item, long userId) {
        if (item.getAvailable() == null) {
            throw new AvailableNotInitInItem();
        }
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new NotFoundException(String.valueOf(userId));
        }
        item.setOwner(userId);
        if (items.containsValue(item)) {
            throw new NewItemException();
        }
        item.setId(id++);
        items.put(item.getId(), item);
        return item;
    }

    public Item updateItem(Map<String, Object> updates, long itemId, long userId) {
        Item item = findItemById(itemId);
        User user = userRepository.findUserById(userId);
        if (item.getOwner() != user.getId()) {
            throw new OwnerException();
        }
        updates.forEach((key, value) -> {
            switch (key) {
                case "id":
                    break;
                case "name":
                    item.setName((String) value);
                    break;
                case "description":
                    item.setDescription((String) value);
                    break;
                case "available":
                    item.setAvailable((Boolean) value);
                    break;
                default:
                    throw new UnsupportedOperationException("Поле '" + key + "' не может быть обновлено");
            }
        });
        return item;
    }

    public Item findItemById(long itemId) {
        if (items.get(itemId) == null) {
            throw new NotFoundException(String.valueOf(itemId));
        }
        return items.get(itemId);
    }
}
