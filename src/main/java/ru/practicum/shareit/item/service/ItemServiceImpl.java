package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.OwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Qualifier("ItemServiceImpl")
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        Item item = ItemMapper.toEntity(itemDto);
        if (userRepository.findById(userId).isPresent()) {
            item.setOwner(userRepository.findById(userId).get());
        } else throw new NotFoundException(String.valueOf(userId));

        return ItemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(ItemDto updateItem, long itemId, long userId) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.valueOf(userId)));
        Item updates = ItemMapper.toEntity(updateItem);
        updates.setOwner(owner);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(String.valueOf(itemId)));

        if (!item.getOwner().equals(owner)) {
            throw new OwnerException();
        }
        ItemMapper.updateEntity(updates, item);

        return ItemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDto findItemById(long itemId) {
        return ItemMapper.toDto(itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(String.valueOf(itemId))));
    }

    @Override
    public List<ItemDto> findItemByUserId(long userId) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.valueOf(userId)));
        return itemRepository.findAllByOwner(owner)
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text)
                .stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }
}
