package ru.practicum.shareit.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.AvailableException;
import ru.practicum.shareit.booking.exception.TimestampException;
import ru.practicum.shareit.booking.exception.UnsupportedStateException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class BookingServiceTest {

    @Mock
    private BookingRepository mockBookingRepository;

    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private ItemRepository mockItemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private BookingDto bookingDto;
    private Long userId;
    private User mockUser;
    private User mockBooker;
    private Item mockItem;

    @BeforeEach
    void setUp() {
        userId = 1L;
        mockUser = new User();
        mockUser.setId(userId);
        mockUser.setName("testName");
        mockUser.setEmail("testEmail");

        // Set up mock item
        mockItem = new Item();
        mockItem.setId(1L);
        mockItem.setName("testName");
        mockItem.setDescription("testDescription");
        mockItem.setAvailable(true);
        mockItem.setOwner(mockUser);

        mockBooker = new User();
        mockBooker.setId(2L);
        mockBooker.setName("testBooker");
        mockBooker.setEmail("testEmail");

        // Set up bookingDto
        bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setItemId(mockItem.getId());
        bookingDto.setStatus(Status.WAITING);
        bookingDto.setItem(mockItem);
        bookingDto.setBooker(mockBooker);

    }

    private Booking makeTestBooking(Status status) {
        User booker = new User(1, "name1", "e@mail1.ru");
        Item item = Item.builder()
                .id(1)
                .owner(mockBooker)
                .name("item name")
                .description("item description")
                .available(true)
                .build();

        return Booking.builder()
                .id(1)
                .booker(booker)
                .item(item)
                .status(status)
                .start(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(1))
                .end(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(2))
                .build();
    }

    @Test
    void createBookingNormalWay() {
        Booking testBooking = makeTestBooking(Status.WAITING);

        when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getBooker()));

        when(mockItemRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getItem()));

        when(mockBookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(testBooking);

        BookingDto booking = bookingService.createBookingRequest(BookingMapper.toDto(testBooking), 1L);

        assertEquals(testBooking.getId(), booking.getId());
    }

    @Test
    void createBookingAvailableException() {
        Booking testBooking = makeTestBooking(Status.WAITING);

        when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getBooker()));

        testBooking.getItem().setAvailable(false);
        when(mockItemRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getItem()));

        when(mockBookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(testBooking);

        final AvailableException exception = Assertions.assertThrows(
                AvailableException.class,
                () -> bookingService.createBookingRequest(BookingMapper.toDto(testBooking), 1L));

        Assertions.assertEquals("Item not available", exception.getMessage());
    }

    @Test
    void createBookingNotFoundException() {
        Booking testBooking = makeTestBooking(Status.WAITING);
        testBooking.getBooker().setId(1L);
        testBooking.getItem().getOwner().setId(1L);

        when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getBooker()));

        when(mockItemRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getItem()));

        when(mockBookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(testBooking);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.createBookingRequest(BookingMapper.toDto(testBooking), 1L));

        Assertions.assertEquals("Объект совпадают owner и booker - 1 не наден", exception.getMessage());
    }

    @Test
    public void testUpdateBookingApproval() {
        Long bookingId = 1L;
        long userId = mockUser.getId();
        boolean approved = true;
        Booking booking = BookingMapper.toEntity(bookingDto);

        User booker = new User();
        booker.setId(userId);

        when(mockBookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(mockUserRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        when(mockBookingRepository.save(any())).thenReturn(booking);

        BookingDto result = bookingService.updateBookingApproval(bookingId, approved, userId);

        assertEquals(Status.APPROVED, booking.getStatus());
        verify(mockBookingRepository, times(1)).save(booking);
    }

    @Test
    public void testUpdateBookingApprovalNotFoundException() {
        Long bookingId = 1L;
        long userId = mockUser.getId();
        boolean approved = true;
        Booking booking = BookingMapper.toEntity(bookingDto);

        User booker = new User();
        booker.setId(userId);

        when(mockBookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(mockUserRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        when(mockBookingRepository.save(any())).thenReturn(booking);

        BookingDto result = bookingService.updateBookingApproval(bookingId, approved, userId);

        final TimestampException exception = assertThrows(
                TimestampException.class,
                () -> bookingService.updateBookingApproval(bookingId, approved, userId));

        assertEquals("Ошибка в указании времени", exception.getMessage());
    }

    @Test
    public void testGetBooking() {
        Booking booking = BookingMapper.toEntity(bookingDto);
        when(mockBookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getBooking(booking.getId(), userId);
    }

    @Test
    void approveBookingNormalWayReject() {
        Booking testBooking = makeTestBooking(Status.WAITING);

        when(mockBookingRepository.findById(1L))
                .thenReturn(Optional.of(testBooking));

        when(mockBookingRepository.save(Mockito.any(Booking.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, Booking.class));
        when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(mockBooker));

        BookingDto booking = bookingService.updateBookingApproval(1L, false, 2);

        testBooking.setStatus(Status.REJECTED);
        assertEquals(testBooking.getId(), booking.getId());
    }

    @Test
    void getBookingsByBookerIdUnsupportedStatus() {
        Booking testBooking = makeTestBooking(Status.WAITING);

        Mockito
                .when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getBooker()));

        final UnsupportedStateException exception = Assertions.assertThrows(
                UnsupportedStateException.class,
                () -> bookingService.getUserBookings(State.UNSUPPORTED_STATUS, 1, 0, 20));

        Assertions.assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }

    @Test
    void getBookingsByOwnerIdUnsupportedStatus() {
        Booking testBooking = makeTestBooking(Status.WAITING);

        when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getBooker()));

        final UnsupportedStateException exception = Assertions.assertThrows(
                UnsupportedStateException.class,
                () -> bookingService.getOwnerBookings(State.UNSUPPORTED_STATUS, 1, 0, 20));

        Assertions.assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }

    @Test
    void getBookingsByBookerIdStatusCurrent() {
        Booking testBooking = makeTestBooking(Status.WAITING);

        when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getBooker()));
        when(mockBookingRepository.findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(
                any(), any(), any())).thenReturn(List.of(testBooking));

        List<BookingDto> bookingDtos = bookingService.getUserBookings(State.CURRENT, 1, null, null);

        assertEquals(1, bookingDtos.size());
    }

    @Test
    void getBookingsByBookerIdStatusPAST() {
        Booking testBooking = makeTestBooking(Status.WAITING);

        when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getBooker()));
        when(mockBookingRepository.findAllByBookerAndEndIsBeforeOrderByEndDesc(
                any(), any())).thenReturn(List.of(testBooking));

        List<BookingDto> bookingDtos = bookingService.getUserBookings(State.PAST, 1, null, null);

        assertEquals(1, bookingDtos.size());
    }

    @Test
    void getBookingsByBookerIdStatusFUTURE() {
        Booking testBooking = makeTestBooking(Status.WAITING);

        when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getBooker()));
        when(mockBookingRepository.findFutureBookingsByBookerOrderByStartDesc(
                any())).thenReturn(List.of(testBooking));

        List<BookingDto> bookingDtos = bookingService.getUserBookings(State.FUTURE, 1, null, null);

        assertEquals(1, bookingDtos.size());
    }

    @Test
    void getBookingsByBookerIdStatusWAITING() {
        Booking testBooking = makeTestBooking(Status.WAITING);

        when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getBooker()));
        when(mockBookingRepository.findAllByBookerAndStatus(
                any(), any())).thenReturn(List.of(testBooking));

        List<BookingDto> bookingDtos = bookingService.getUserBookings(State.WAITING, 1, null, null);

        assertEquals(1, bookingDtos.size());
    }

    @Test
    void getBookingsByBookerIdStatusREJECTED() {
        Booking testBooking = makeTestBooking(Status.WAITING);

        when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getBooker()));
        when(mockBookingRepository.findAllByBookerAndStatus(
                any(), any())).thenReturn(List.of(testBooking));

        List<BookingDto> bookingDtos = bookingService.getUserBookings(State.REJECTED, 1, null, null);

        assertEquals(1, bookingDtos.size());
    }

    @Test
    void getBookingsByBookerIdStatusALL() {
        Booking testBooking = makeTestBooking(Status.WAITING);

        when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getBooker()));
        when(mockBookingRepository.findAllBookingsByBookerOrderByStartDesc(
                any())).thenReturn(List.of(testBooking));

        List<BookingDto> bookingDtos = bookingService.getUserBookings(State.ALL, 1, null, null);

        assertEquals(1, bookingDtos.size());
    }

    @Test
    void getBookingsByBookerIdUnsupportedStatusList() {
        Booking testBooking = makeTestBooking(Status.WAITING);

        Mockito
                .when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getBooker()));

        final UnsupportedStateException exception = Assertions.assertThrows(
                UnsupportedStateException.class,
                () -> bookingService.getUserBookings(State.UNSUPPORTED_STATUS, 1, null, null));

        Assertions.assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }

    @Test
    void getBookingsByOwnerStatusPAST() {
        Booking testBooking = makeTestBooking(Status.WAITING);

        when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getBooker()));
        when(mockBookingRepository.findAllByItemOwnerAndEndIsBeforeOrderByEndDesc(
                any(), any())).thenReturn(List.of(testBooking));

        List<BookingDto> bookingDtos = bookingService.getOwnerBookings(State.PAST, 1, null, null);

        assertEquals(1, bookingDtos.size());
    }

    @Test
    void getBookingsByOwnerStatusFUTURE() {
        Booking testBooking = makeTestBooking(Status.WAITING);

        when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getBooker()));
        when(mockBookingRepository.findFutureBookingsByItemOwnerOrderByStartDesc(
                any())).thenReturn(List.of(testBooking));

        List<BookingDto> bookingDtos = bookingService.getOwnerBookings(State.FUTURE, 1, null, null);

        assertEquals(1, bookingDtos.size());
    }

    @Test
    void getBookingsByOwnerStatusWAITING() {
        Booking testBooking = makeTestBooking(Status.WAITING);

        when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getBooker()));
        when(mockBookingRepository.findAllByItemOwnerAndStatus(
                any(), any())).thenReturn(List.of(testBooking));

        List<BookingDto> bookingDtos = bookingService.getOwnerBookings(State.WAITING, 1, null, null);

        assertEquals(1, bookingDtos.size());
    }

    @Test
    void getBookingsByOwnerStatusREJECTED() {
        Booking testBooking = makeTestBooking(Status.WAITING);

        when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getBooker()));
        when(mockBookingRepository.findAllByItemOwnerAndStatus(
                any(), any())).thenReturn(List.of(testBooking));

        List<BookingDto> bookingDtos = bookingService.getOwnerBookings(State.REJECTED, 1, null, null);

        assertEquals(1, bookingDtos.size());
    }

    @Test
    void getBookingsByOwnerStatusALL() {
        Booking testBooking = makeTestBooking(Status.WAITING);

        when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getBooker()));
        when(mockBookingRepository.findAllBookingsByItemOwnerOrderByStartDesc(
                any())).thenReturn(List.of(testBooking));

        List<BookingDto> bookingDtos = bookingService.getOwnerBookings(State.ALL, 1, null, null);

        assertEquals(1, bookingDtos.size());
    }

    @Test
    void getBookingsByOwnerIdUnsupportedStatusList() {
        Booking testBooking = makeTestBooking(Status.WAITING);

        Mockito
                .when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getBooker()));

        final UnsupportedStateException exception = Assertions.assertThrows(
                UnsupportedStateException.class,
                () -> bookingService.getOwnerBookings(State.UNSUPPORTED_STATUS, 1, null, null));

        Assertions.assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }

    @Test
    void getBookingsByOwnerStatusPASTPage() {
        Booking testBooking = makeTestBooking(Status.WAITING);

        when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getBooker()));
        when(mockBookingRepository.findAllByItemOwnerAndEndIsBeforeOrderByEndDesc(
                any(), any(), any())).thenReturn(new PageImpl<>(List.of(testBooking)));

        List<BookingDto> bookingDtos = bookingService.getOwnerBookings(State.PAST, 1, 0, 20);

        assertEquals(1, bookingDtos.size());
    }

    @Test
    void getBookingsByOwnerStatusFUTUREPage() {
        Booking testBooking = makeTestBooking(Status.WAITING);

        when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getBooker()));
        when(mockBookingRepository.findFutureBookingsByItemOwnerOrderByStartDesc(
                any(), any())).thenReturn(new PageImpl<>(List.of(testBooking)));

        List<BookingDto> bookingDtos = bookingService.getOwnerBookings(State.FUTURE, 1, 0, 20);

        assertEquals(1, bookingDtos.size());
    }

    @Test
    void getBookingsByOwnerStatusWAITINGPage() {
        Booking testBooking = makeTestBooking(Status.WAITING);

        when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getBooker()));
        when(mockBookingRepository.findAllByItemOwnerAndStatus(
                any(), any(), any())).thenReturn(new PageImpl<>(List.of(testBooking)));

        List<BookingDto> bookingDtos = bookingService.getOwnerBookings(State.WAITING, 1, 0, 20);

        assertEquals(1, bookingDtos.size());
    }

    @Test
    void getBookingsByOwnerStatusREJECTEDPage() {
        Booking testBooking = makeTestBooking(Status.WAITING);

        when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getBooker()));
        when(mockBookingRepository.findAllByItemOwnerAndStatus(
                any(), any(), any())).thenReturn(new PageImpl<>(List.of(testBooking)));

        List<BookingDto> bookingDtos = bookingService.getOwnerBookings(State.REJECTED, 1, 0, 20);

        assertEquals(1, bookingDtos.size());
    }

    @Test
    void getBookingsByOwnerStatusALLPage() {
        Booking testBooking = makeTestBooking(Status.WAITING);

        when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getBooker()));
        when(mockBookingRepository.findAllBookingsByItemOwnerOrderByStartDesc(
                any(), any())).thenReturn(new PageImpl<>(List.of(testBooking)));

        List<BookingDto> bookingDtos = bookingService.getOwnerBookings(State.ALL, 1, 0, 20);

        assertEquals(1, bookingDtos.size());
    }

    @Test
    void getBookingsByOwnerIdUnsupportedStatusPage() {
        Booking testBooking = makeTestBooking(Status.WAITING);

        Mockito
                .when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getBooker()));

        final UnsupportedStateException exception = Assertions.assertThrows(
                UnsupportedStateException.class,
                () -> bookingService.getOwnerBookings(State.UNSUPPORTED_STATUS, 1, 0, 20));

        Assertions.assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }

    @Test
    void getBookingsByUserStatusPASTPage() {
        Booking testBooking = makeTestBooking(Status.WAITING);

        when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getBooker()));
        when(mockBookingRepository.findAllByBookerAndEndIsBeforeOrderByEndDesc(
                any(), any(), any())).thenReturn(new PageImpl<>(List.of(testBooking)));

        List<BookingDto> bookingDtos = bookingService.getUserBookings(State.PAST, 1, 0, 20);

        assertEquals(1, bookingDtos.size());
    }

    @Test
    void getBookingsByUserStatusFUTUREPage() {
        Booking testBooking = makeTestBooking(Status.WAITING);

        when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getBooker()));
        when(mockBookingRepository.findFutureBookingsByBookerOrderByStartDesc(
                any(), any())).thenReturn(new PageImpl<>(List.of(testBooking)));

        List<BookingDto> bookingDtos = bookingService.getUserBookings(State.FUTURE, 1, 0, 20);

        assertEquals(1, bookingDtos.size());
    }

    @Test
    void getBookingsByUserStatusWAITINGPage() {
        Booking testBooking = makeTestBooking(Status.WAITING);

        when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getBooker()));
        when(mockBookingRepository.findAllByBookerAndStatus(
                any(), any(), any())).thenReturn(new PageImpl<>(List.of(testBooking)));

        List<BookingDto> bookingDtos = bookingService.getUserBookings(State.WAITING, 1, 0, 20);

        assertEquals(1, bookingDtos.size());
    }

    @Test
    void getBookingsByUserStatusREJECTEDPage() {
        Booking testBooking = makeTestBooking(Status.WAITING);

        when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getBooker()));
        when(mockBookingRepository.findAllByBookerAndStatus(
                any(), any(), any())).thenReturn(new PageImpl<>(List.of(testBooking)));

        List<BookingDto> bookingDtos = bookingService.getUserBookings(State.REJECTED, 1, 0, 20);

        assertEquals(1, bookingDtos.size());
    }

    @Test
    void getBookingsByUserStatusALLPage() {
        Booking testBooking = makeTestBooking(Status.WAITING);

        when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getBooker()));
        when(mockBookingRepository.findAllBookingsByBookerOrderByStartDesc(
                any(), any())).thenReturn(new PageImpl<>(List.of(testBooking)));

        List<BookingDto> bookingDtos = bookingService.getUserBookings(State.ALL, 1, 0, 20);

        assertEquals(1, bookingDtos.size());
    }

    @Test
    void getBookingsByUserIdUnsupportedStatusPage() {
        Booking testBooking = makeTestBooking(Status.WAITING);

        Mockito
                .when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getBooker()));

        final UnsupportedStateException exception = Assertions.assertThrows(
                UnsupportedStateException.class,
                () -> bookingService.getUserBookings(State.UNSUPPORTED_STATUS, 1, 0, 20));

        Assertions.assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }
}