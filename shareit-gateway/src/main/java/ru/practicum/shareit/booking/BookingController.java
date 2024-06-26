package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.validation.CreateBookingValidation;

import javax.validation.constraints.Positive;
import java.util.EnumSet;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBookingRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @Validated(CreateBookingValidation.class)
                                                    @RequestBody BookingDto bookingDto) {
        log.info("Creating booking {}, userId={}", bookingDto, userId);
        return bookingClient.addBookingRequest(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> updateBookingApproval(@Positive @PathVariable Long bookingId, @RequestParam boolean approved,
                                                        @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Approve booking {}, ownerId {}, approved {}", bookingId, userId, approved);

        return bookingClient.updateBookingApproval(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@Positive @PathVariable Long bookingId,
                                             @Positive @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(@RequestParam(defaultValue = "ALL") State state,
                                                  @RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam(required = false) Integer from,
                                                  @RequestParam(required = false) Integer size) {
        log.info("Get booking with state {}, userId={}, from={}, size={}", state, userId, from, size);
        if (!EnumSet.allOf(State.class).contains(state)) {
            throw new IllegalArgumentException("State " + state + " is not supported");
        }
        return bookingClient.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@RequestParam(defaultValue = "ALL") State state,
                                                   @RequestHeader("X-Sharer-User-Id") long userId,
                                                   @RequestParam(required = false) Integer from,
                                                   @RequestParam(required = false) Integer size) {
        log.info("Get booking of items of owner {} with state {}, from={}, size={}", userId, state, from, size);
        if (!EnumSet.allOf(State.class).contains(state)) {
            throw new IllegalArgumentException("State " + state + " is not supported");
        }
        return bookingClient.getOwnerBookings(userId, state, from, size);
    }

}
