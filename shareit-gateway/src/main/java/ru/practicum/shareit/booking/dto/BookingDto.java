package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.validation.CreateBookingValidation;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDto {
    private long id;
    @FutureOrPresent(groups = CreateBookingValidation.class)
    @NotNull(groups = CreateBookingValidation.class)
    private LocalDateTime start;
    @FutureOrPresent(groups = CreateBookingValidation.class)
    @NotNull(groups = CreateBookingValidation.class)
    private LocalDateTime end;
    //private Item item;
    //private User booker;
    private Status status;
    @NotNull(groups = CreateBookingValidation.class)
    private Long itemId;
}