package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.AvailableException;
import ru.practicum.shareit.booking.exception.TimestampException;
import ru.practicum.shareit.booking.exception.UnsupportedStateException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
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
        if (booker.getId() == item.getOwner().getId()) {
            throw new NotFoundException(String.valueOf(bookingDto.getItemId()));
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
    public List<BookingDto> getUserBookings(State state, long userId, Integer from, Integer size) {
        User booker = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.valueOf(userId)));
        LocalDateTime currentTime = LocalDateTime.now();
        if (from == null || size == null) {
            switch (state) {
                case CURRENT:
                    return bookingRepository.findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(
                            booker, currentTime, currentTime).stream()
                            .map(BookingMapper::toDto).collect(Collectors.toList());
                case PAST:
                    return bookingRepository.findAllByBookerAndEndIsBeforeOrderByEndDesc(booker, currentTime).stream()
                            .map(BookingMapper::toDto).collect(Collectors.toList());
                case FUTURE:
                    return bookingRepository.findFutureBookingsByBookerOrderByStartDesc(booker).stream()
                            .map(BookingMapper::toDto).collect(Collectors.toList());
                case WAITING:
                    return bookingRepository.findAllByBookerAndStatus(booker, Status.WAITING).stream()
                            .map(BookingMapper::toDto).collect(Collectors.toList());
                case REJECTED:
                    return bookingRepository.findAllByBookerAndStatus(booker, Status.REJECTED).stream()
                            .map(BookingMapper::toDto).collect(Collectors.toList());
                case ALL:
                    return bookingRepository.findAllBookingsByBookerOrderByStartDesc(booker).stream()
                            .map(BookingMapper::toDto).collect(Collectors.toList());
                default:
                    throw new UnsupportedStateException(state.toString());
            }
        } else {
            ///////fix////////
            Pageable page;
            if (from < 0 || size < 0) {
                page = PageRequest.of(from, size);
            } else page = PageRequest.of(from / size, size);
            /////////////////

            switch (state) {
                case CURRENT:
                    return bookingRepository.findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(
                            booker, currentTime, currentTime, page).stream()
                            .map(BookingMapper::toDto).collect(Collectors.toList());
                case PAST:
                    return bookingRepository.findAllByBookerAndEndIsBeforeOrderByEndDesc(booker, currentTime, page)
                            .stream()
                            .map(BookingMapper::toDto).collect(Collectors.toList());
                case FUTURE:
                    return bookingRepository.findFutureBookingsByBookerOrderByStartDesc(booker, page).stream()
                            .map(BookingMapper::toDto).collect(Collectors.toList());
                case WAITING:
                    return bookingRepository.findAllByBookerAndStatus(booker, Status.WAITING, page).stream()
                            .map(BookingMapper::toDto).collect(Collectors.toList());
                case REJECTED:
                    return bookingRepository.findAllByBookerAndStatus(booker, Status.REJECTED, page).stream()
                            .map(BookingMapper::toDto).collect(Collectors.toList());
                case ALL:
                    return bookingRepository.findAllBookingsByBookerOrderByStartDesc(booker, page).stream()
                            .map(BookingMapper::toDto).collect(Collectors.toList());
                default:
                    throw new UnsupportedStateException(state.toString());
            }
        }
    }

    @Override
    public List<BookingDto> getOwnerBookings(State state, long userId, Integer from, Integer size) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.valueOf(userId)));
        LocalDateTime currentTime = LocalDateTime.now();
        if (from == null || size == null) {
            switch (state) {
                case CURRENT:
                    return bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(
                                    owner, currentTime, currentTime).stream()
                            .map(BookingMapper::toDto).collect(Collectors.toList());
                case PAST:
                    return bookingRepository.findAllByItemOwnerAndEndIsBeforeOrderByEndDesc(owner, currentTime).stream()
                            .map(BookingMapper::toDto).collect(Collectors.toList());
                case FUTURE:
                    return bookingRepository.findFutureBookingsByItemOwnerOrderByStartDesc(owner).stream()
                            .map(BookingMapper::toDto).collect(Collectors.toList());
                case WAITING:
                    return bookingRepository.findAllByItemOwnerAndStatus(owner, Status.WAITING).stream()
                            .map(BookingMapper::toDto).collect(Collectors.toList());
                case REJECTED:
                    return bookingRepository.findAllByItemOwnerAndStatus(owner, Status.REJECTED).stream()
                            .map(BookingMapper::toDto).collect(Collectors.toList());
                case ALL:
                    return bookingRepository.findAllBookingsByItemOwnerOrderByStartDesc(owner).stream()
                            .map(BookingMapper::toDto).collect(Collectors.toList());
                default:
                    throw new UnsupportedStateException(state.toString());
            }
        } else {
            Pageable page = PageRequest.of(from, size);
            switch (state) {
                case CURRENT:
                    return bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(
                                    owner, currentTime, currentTime, page).stream()
                            .map(BookingMapper::toDto).collect(Collectors.toList());
                case PAST:
                    return bookingRepository.findAllByItemOwnerAndEndIsBeforeOrderByEndDesc(owner, currentTime, page).stream()
                            .map(BookingMapper::toDto).collect(Collectors.toList());
                case FUTURE:
                    return bookingRepository.findFutureBookingsByItemOwnerOrderByStartDesc(owner, page).stream()
                            .map(BookingMapper::toDto).collect(Collectors.toList());
                case WAITING:
                    return bookingRepository.findAllByItemOwnerAndStatus(owner, Status.WAITING, page).stream()
                            .map(BookingMapper::toDto).collect(Collectors.toList());
                case REJECTED:
                    return bookingRepository.findAllByItemOwnerAndStatus(owner, Status.REJECTED, page).stream()
                            .map(BookingMapper::toDto).collect(Collectors.toList());
                case ALL:
                    return bookingRepository.findAllBookingsByItemOwnerOrderByStartDesc(owner, page).stream()
                            .map(BookingMapper::toDto).collect(Collectors.toList());
                default:
                    throw new UnsupportedStateException(state.toString());
            }
        }
    }
}
