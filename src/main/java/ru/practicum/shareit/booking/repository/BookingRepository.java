package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findFirstByItemIdAndEndIsBeforeOrderByEndDesc(Long itemId, LocalDateTime end);

    Optional<Booking> findFirstByItemIdAndStartIsAfterOrderByStartAsc(Long itemId, LocalDateTime start);

    List<Booking> findCurrentBookingsByBookerOrderByStartDesc(User booker);

    List<Booking> findPastBookingsByBookerOrderByStartDesc(User booker);

    List<Booking> findFutureBookingsByBookerOrderByStartDesc(User booker);

    List<Booking> findAllBookingsByBookerOrderByStartDesc(User booker);

    List<Booking> findCurrentBookingsByItemOwnerOrderByStartDesc(User owner);

    List<Booking> findPastBookingsByItemOwnerOrderByStartDesc(User owner);

    List<Booking> findFutureBookingsByItemOwnerOrderByStartDesc(User owner);

    List<Booking> findAllBookingsByItemOwnerOrderByStartDesc(User owner);

    List<Booking> findAllByItemOwnerAndStatus(User owner, Status status);

    List<Booking> findAllByBookerAndStatus(User booker, Status status);

    List<Booking> findByBooker_Id(Long bookerId);
}
