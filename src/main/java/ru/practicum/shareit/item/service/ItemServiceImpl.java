package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithTime;
import ru.practicum.shareit.item.exception.OwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.model.LastBooking;
import ru.practicum.shareit.item.model.NextBooking;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
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
    private final BookingRepository bookingRepository;

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
    public ItemDtoWithTime findItemById(long itemId, long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(String.valueOf(itemId)));
        ItemDtoWithTime itemDtoWithTime = ItemMapper.toDtoWithTime(item);
        LastBooking lastBooking;
        NextBooking nextBooking;
        if (userId != item.getOwner().getId()) {
            lastBooking = null;
            nextBooking = null;
        } else {
            LocalDateTime currentTime = LocalDateTime.now();
            lastBooking = calculateLastBooking(itemId, currentTime);
            nextBooking = calculateNextBooking(itemId, currentTime);
        }
        itemDtoWithTime.setLastBooking(lastBooking);
        itemDtoWithTime.setNextBooking(nextBooking);
        return itemDtoWithTime;
    }

    private LastBooking calculateLastBooking(long itemId, LocalDateTime currentTime) {
        LastBooking lastBooking = new LastBooking();
        Booking nowBooking = bookingRepository.findFirstByItemIdAndEndIsBeforeOrderByEndDesc(itemId, currentTime).orElse(null);
        if (nowBooking != null) {
            lastBooking.setId(nowBooking.getId());
            lastBooking.setBookerId(nowBooking.getBooker().getId());
            lastBooking.setLastTime(nowBooking.getEnd());
        } else lastBooking = null;
        return lastBooking;
    }

    private NextBooking calculateNextBooking(long itemId, LocalDateTime currentTime) {
        NextBooking nextBooking = new NextBooking();
        Booking afterBooking = bookingRepository.findFirstByItemIdAndStartIsAfterOrderByStartAsc(itemId, currentTime).orElse(null);
        if (afterBooking != null) {
            nextBooking.setId(afterBooking.getId());
            nextBooking.setBookerId(afterBooking.getBooker().getId());
            nextBooking.setNextTime(afterBooking.getStart());
        } else nextBooking = null;

        return nextBooking;
    }

    @Override
    public List<ItemDtoWithTime> findItemByUserId(long userId) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.valueOf(userId)));
        List<Item> items = itemRepository.findAllByOwner(owner);

        List<ItemDtoWithTime> itemDtoWithTimeList = new ArrayList<>();

        LocalDateTime currentTime = LocalDateTime.now();

        for (Item item : items) {
            ItemDtoWithTime itemDtoWithTime = ItemMapper.toDtoWithTime(item);

            LastBooking lastBooking = calculateLastBooking(item.getId(), currentTime);
            NextBooking nextBooking = calculateNextBooking(item.getId(), currentTime);

            itemDtoWithTime.setLastBooking(lastBooking);
            itemDtoWithTime.setNextBooking(nextBooking);

            itemDtoWithTimeList.add(itemDtoWithTime);
        }

        return itemDtoWithTimeList;
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
