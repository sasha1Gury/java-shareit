package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBookingRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @RequestBody BookingDto bookingDto) {
        return bookingService.createBookingRequest(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBookingApproval(@PathVariable Long bookingId, @RequestParam boolean approved,
                                            @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.updateBookingApproval(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable Long bookingId,
                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(@RequestParam(defaultValue = "ALL") State state,
                                            @RequestHeader("X-Sharer-User-Id") long userId,
                                            @RequestParam(required = false) Integer from,
                                            @RequestParam(required = false) Integer size) {
        return bookingService.getUserBookings(state, userId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestParam(defaultValue = "ALL") State state,
                                             @RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestParam(required = false) Integer from,
                                             @RequestParam(required = false) Integer size) {
        return bookingService.getOwnerBookings(state, userId, from, size);
    }
}
