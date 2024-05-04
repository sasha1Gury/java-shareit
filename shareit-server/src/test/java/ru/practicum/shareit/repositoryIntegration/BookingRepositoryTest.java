package ru.practicum.shareit.repositoryIntegration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.practicum.shareit.booking.model.Status.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookingRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private BookingRepository bookingRepository;

    private User bookerUser;
    private User ownerUser;
    private Booking approvedBooking;
    private Booking waitingBooking;
    private Booking rejectedBooking;

    @BeforeEach
    void beforeEach() {
        bookerUser = em.persistAndFlush(User.builder().name("booker").email("booker@email.ru").build());
        ownerUser = em.persistAndFlush(User.builder().name("owner").email("owner@email.ru").build());
        Item itemToBook = em.persistAndFlush(Item.builder().name("компрессор").description("мощный компрессор")
                .available(true).owner(ownerUser).build());

        LocalDateTime nowHour = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
        approvedBooking = em.persistAndFlush(Booking.builder().booker(bookerUser).item(itemToBook)
                .start(nowHour.minusHours(3))
                .end(nowHour.minusHours(2))
                .status(APPROVED).build());
        waitingBooking = em.persistAndFlush(Booking.builder().booker(bookerUser).item(itemToBook)
                .start(nowHour.plusHours(3))
                .end(nowHour.plusHours(4))
                .status(WAITING).build());
        rejectedBooking = em.persistAndFlush(Booking.builder().booker(bookerUser).item(itemToBook)
                .start(nowHour.plusHours(1))
                .end(nowHour.plusHours(2))
                .status(REJECTED).build());
    }

    @Test
    void findByBooker_idOrderByRentStartDateDescTest() {
        Pageable page = PageRequest.of(0, 20);
        Page<Booking> bookingPage = bookingRepository.findAllBookingsByBookerOrderByStartDesc(bookerUser, page);
        List<Booking> bookingList = bookingPage.toList();

        assertEquals(3, bookingList.size());
        assertEquals(waitingBooking.getId(), bookingList.get(0).getId());
        assertEquals(rejectedBooking.getId(), bookingList.get(1).getId());
        assertEquals(approvedBooking.getId(), bookingList.get(2).getId());
    }

    @Test
    void findByBooker_idAndStatusOrderByRentStartDateDescTest() {
        Pageable page = PageRequest.of(0, 20);
        Page<Booking> bookingPage = bookingRepository
                .findAllByBookerAndStatus(bookerUser, REJECTED, page);

        List<Booking> bookingList = bookingPage.toList();
        assertEquals(1, bookingList.size());
        assertEquals(rejectedBooking.getId(), bookingList.get(0).getId());
    }

    @Test
    void findBookingsOfItemsByOwnerIdTest() {
        Pageable page = PageRequest.of(0, 20);
        Page<Booking> bookingPage = bookingRepository.findAllBookingsByItemOwnerOrderByStartDesc(ownerUser, page);

        List<Booking> bookingList = bookingPage.toList();
        assertEquals(3, bookingList.size());
        assertEquals(waitingBooking.getId(), bookingList.get(0).getId());
        assertEquals(rejectedBooking.getId(), bookingList.get(1).getId());
        assertEquals(approvedBooking.getId(), bookingList.get(2).getId());
    }

    @Test
    void findBookingsOfItemsByOwnerIdAndStatusTest() {
        Pageable page = PageRequest.of(0, 20);
        Page<Booking> bookingPage = bookingRepository
                .findAllByItemOwnerAndStatus(ownerUser, APPROVED, page);

        List<Booking> bookingList = bookingPage.toList();
        assertEquals(1, bookingList.size());
        assertEquals(approvedBooking.getId(), bookingList.get(0).getId());
    }

    @Test
    void findBookingsOfItemsByOwnerIdInPastTest() {
        Pageable page = PageRequest.of(0, 20);
        Page<Booking> bookingPage = bookingRepository
                .findAllByItemOwnerAndEndIsBeforeOrderByEndDesc(ownerUser, LocalDateTime.now(), page);

        List<Booking> bookingList = bookingPage.toList();
        assertEquals(1, bookingList.size());
        assertEquals(approvedBooking.getId(), bookingList.get(0).getId());
    }

    @Test
    void findBookingsOfItemsByOwnerIdInCurrentTest() {
        Pageable page = PageRequest.of(0, 20);
        Page<Booking> bookingPage = bookingRepository
                .findAllByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(ownerUser, LocalDateTime.now(),
                        LocalDateTime.now(), page);

        List<Booking> bookingList = bookingPage.toList();
        assertTrue(bookingList.isEmpty());
    }

    @Test
    void findByBooker_idAndRentStartDateBeforeAndRentEndDateAfterOrderByRentStartDateDescTest() {
        Pageable page = PageRequest.of(0, 20);
        Page<Booking> bookingPage = bookingRepository
                .findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(bookerUser,
                        LocalDateTime.now(), LocalDateTime.now(), page);

        List<Booking> bookingList = bookingPage.toList();
        assertTrue(bookingList.isEmpty());
    }

    @Test
    void findByBooker_idAndRentEndDateBeforeOrderByRentStartDateDescTest() {
        Pageable page = PageRequest.of(0, 20);
        Page<Booking> bookingPage = bookingRepository
                .findAllByBookerAndEndIsBeforeOrderByEndDesc(bookerUser, LocalDateTime.now(), page);

        List<Booking> bookingList = bookingPage.toList();
        assertEquals(1, bookingList.size());
        assertEquals(approvedBooking.getId(), bookingList.get(0).getId());
    }

}
