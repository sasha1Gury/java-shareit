package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {
    BookingDto createBookingRequest(BookingDto booking, Long userId);

    BookingDto updateBookingApproval(Long bookingId, boolean approved, long userId);

    BookingDto getBooking(Long bookingId, long userId);

    List<BookingDto> getUserBookings(State state, long userId);

    List<BookingDto> getOwnerBookings(State state, long userId);
}
