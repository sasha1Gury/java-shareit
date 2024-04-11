package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByItemIdOrderByStartDesc(Long itemId);

    List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime end);

    Optional<Booking> findFirstByItemIdAndEndIsBeforeOrderByEndDesc(Long itemId, LocalDateTime end);

    Optional<Booking> findFirstByItemIdAndStartIsAfterOrderByStartAsc(Long itemId, LocalDateTime start);

    List<Booking> findCurrentBookingsByBookerOrderByStartDesc(User booker);

    List<Booking> findPastBookingsByBookerOrderByStartDesc(User booker);

    List<Booking> findFutureBookingsByBookerOrderByStartDesc(User booker);

    List<Booking> findWaitingBookingsByBookerOrderByStartDesc(User booker);

    List<Booking> findRejectedBookingsByBookerOrderByStartDesc(User booker);

    List<Booking> findAllBookingsByBookerOrderByStartDesc(User booker);

    List<Booking> findCurrentBookingsByItemOwnerOrderByStartDesc(User owner);

    List<Booking> findPastBookingsByItemOwnerOrderByStartDesc(User owner);

    List<Booking> findFutureBookingsByItemOwnerOrderByStartDesc(User owner);

    List<Booking> findWaitingBookingsByItemOwnerOrderByStartDesc(User owner);

    List<Booking> findRejectedBookingsByItemOwnerOrderByStartDesc(User owner);

    List<Booking> findAllBookingsByItemOwnerOrderByStartDesc(User owner);
}
