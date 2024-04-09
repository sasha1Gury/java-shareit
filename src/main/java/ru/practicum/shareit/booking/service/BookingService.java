package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

public interface BookingService {
    BookingDto createBookingRequest(BookingDto booking, Long userId);
}
