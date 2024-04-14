package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.validation.CreateBookingValidation;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@AllArgsConstructor
public class BookingDto {
    private long id;
    @FutureOrPresent(groups = CreateBookingValidation.class)
    @NotNull(groups = CreateBookingValidation.class)
    private LocalDateTime start;
    @FutureOrPresent(groups = CreateBookingValidation.class)
    @NotNull(groups = CreateBookingValidation.class)
    private LocalDateTime end;
    private Item item;
    private User booker;
    private Status status;
    @NotNull(groups = CreateBookingValidation.class)
    private Long itemId;
}
