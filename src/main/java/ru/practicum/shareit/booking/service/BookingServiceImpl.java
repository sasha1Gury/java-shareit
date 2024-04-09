package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.AvailableException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto createBookingRequest(BookingDto bookingDto, Long userId) {
        Booking booking = BookingMapper.toEntity(bookingDto);
        User booker = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.valueOf(userId)));
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new NotFoundException(
                String.valueOf(bookingDto.getItem().getId())));
        if (!item.getAvailable()) {
            throw new AvailableException();
        }
        booking.setStatus(Status.WAITING);
        booking.setBooker(booker);
        booking.setItem(item);
        return BookingMapper.toDto(bookingRepository.save(booking));
    }
}
