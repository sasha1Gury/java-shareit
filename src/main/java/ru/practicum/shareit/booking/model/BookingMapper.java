package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;

public class BookingMapper {
    public static Booking toEntity(BookingDto dto) {
        return new Booking(
                dto.getId(),
                dto.getStart(),
                dto.getEnd(),
                dto.getItem(),
                dto.getBooker(),
                dto.getStatus()
        );
    }

    public static BookingDto toDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus()
        );
    }
}
