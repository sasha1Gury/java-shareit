package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findFirstByItemIdAndEndIsBeforeAndStatusOrderByEndDesc(Long itemId, LocalDateTime end, Status status);

    Optional<Booking> findFirstByItemIdAndStartIsAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime start, Status status);

    List<Booking> findFutureBookingsByBookerOrderByStartDesc(User booker);

    List<Booking> findAllBookingsByBookerOrderByStartDesc(User booker);

    List<Booking> findFutureBookingsByItemOwnerOrderByStartDesc(User owner);

    List<Booking> findAllBookingsByItemOwnerOrderByStartDesc(User owner);

    List<Booking> findAllByItemOwnerAndStatus(User owner, Status status);

    List<Booking> findAllByBookerAndStatus(User booker, Status status);

    List<Booking> findByBooker_Id(Long bookerId);

    List<Booking> findAllByBookerAndEndIsBeforeOrderByEndDesc(User booker, LocalDateTime localDateTime);

    List<Booking> findAllByItemOwnerAndEndIsBeforeOrderByEndDesc(User owner, LocalDateTime localDateTime);

    List<Booking> findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(User booker, LocalDateTime cur, LocalDateTime current);

    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(User owner, LocalDateTime cur, LocalDateTime current);

    Optional<Booking> findFirstByItemIdAndStartBeforeAndEndAfterAndStatusOrderByStartDesc(Long itemId, LocalDateTime start, LocalDateTime current, Status status);
}
