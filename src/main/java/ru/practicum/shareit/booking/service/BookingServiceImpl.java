package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.AvailableException;
import ru.practicum.shareit.booking.exception.TimestampException;
import ru.practicum.shareit.booking.exception.UnsupportedStateException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

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
                String.valueOf(bookingDto.getItemId())));
        if (!item.getAvailable()) {
            throw new AvailableException();
        }
        if (booking.getEnd().isBefore(booking.getStart()) || booking.getEnd().isEqual(booking.getStart())) {
            throw new TimestampException();
        }
        booking.setStatus(Status.WAITING);
        booking.setBooker(booker);
        booking.setItem(item);
        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto updateBookingApproval(Long bookingId, boolean approved, long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException(String.valueOf(bookingId)));
        User booker = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.valueOf(userId)));
        if (!booking.getItem().getOwner().equals(booker)) {
            throw new NotFoundException(String.valueOf(bookingId));
        }
        if (booking.getStatus() != Status.WAITING) {
            throw new TimestampException();
        }
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);

        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBooking(Long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException(String.valueOf(bookingId)));
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException(String.valueOf(bookingId));
        }
        return BookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(String state, long userId) {
        User booker = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.valueOf(userId)));

        switch (state) {
            case "CURRENT":
                return bookingRepository.findCurrentBookingsByBookerOrderByStartDesc(booker).stream()
                        .map(BookingMapper::toDto).collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findPastBookingsByBookerOrderByStartDesc(booker).stream()
                        .map(BookingMapper::toDto).collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findFutureBookingsByBookerOrderByStartDesc(booker).stream()
                        .map(BookingMapper::toDto).collect(Collectors.toList());
            case "WAITING":
                return bookingRepository.findWaitingBookingsByBookerOrderByStartDesc(booker).stream()
                        .map(BookingMapper::toDto).collect(Collectors.toList());
            case "REJECTED":
                return bookingRepository.findRejectedBookingsByBookerOrderByStartDesc(booker).stream()
                        .map(BookingMapper::toDto).collect(Collectors.toList());
            case "ALL":
                return bookingRepository.findAllBookingsByBookerOrderByStartDesc(booker).stream()
                        .map(BookingMapper::toDto).collect(Collectors.toList());
            default:
                throw new UnsupportedStateException(state);
        }
    }

    @Override
    public List<BookingDto> getOwnerBookings(String state, long userId) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.valueOf(userId)));

        switch (state) {
            case "CURRENT":
                return bookingRepository.findCurrentBookingsByItemOwnerOrderByStartDesc(owner).stream()
                        .map(BookingMapper::toDto).collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findPastBookingsByItemOwnerOrderByStartDesc(owner).stream()
                        .map(BookingMapper::toDto).collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findFutureBookingsByItemOwnerOrderByStartDesc(owner).stream()
                        .map(BookingMapper::toDto).collect(Collectors.toList());
            case "WAITING":
                return bookingRepository.findWaitingBookingsByItemOwnerOrderByStartDesc(owner).stream()
                        .map(BookingMapper::toDto).collect(Collectors.toList());
            case "REJECTED":
                return bookingRepository.findRejectedBookingsByItemOwnerOrderByStartDesc(owner).stream()
                        .map(BookingMapper::toDto).collect(Collectors.toList());
            case "ALL":
                return bookingRepository.findAllBookingsByItemOwnerOrderByStartDesc(owner).stream()
                        .map(BookingMapper::toDto).collect(Collectors.toList());
            default:
                throw new UnsupportedStateException(state);
        }
    }
}
