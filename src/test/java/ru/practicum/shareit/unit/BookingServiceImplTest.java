package ru.practicum.shareit.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@SpringBootTest
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private BookingDto bookingDto;
    private Long userId;
    private User mockUser;
    private User mockBooker;
    private Item mockItem;

    @BeforeEach
    void setUp() {
        // Set up userId and mock user
        userId = 1L;
        mockUser = new User(); // Set up a mock user
        mockUser.setId(userId);
        mockUser.setName("testName");
        mockUser.setEmail("testEmail");

        // Set up mock item
        mockItem = new Item(); // Set up a mock item
        mockItem.setId(1L);
        mockItem.setName("testName");
        mockItem.setDescription("testDescription");
        mockItem.setAvailable(true);
        mockItem.setOwner(mockUser);

        mockBooker = new User(); // Set up a mock item
        mockBooker.setId(2L);
        mockBooker.setName("testBooker");
        mockBooker.setEmail("testEmail");

        // Set up bookingDto
        bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1)); // Set start time
        bookingDto.setEnd(LocalDateTime.now().plusDays(2)); // Set end time
        bookingDto.setItemId(mockItem.getId()); // Set item id
        bookingDto.setStatus(Status.WAITING);
        bookingDto.setItem(mockItem);
        bookingDto.setBooker(mockBooker);

    }

    @Test
    void testCreateBookingRequest() {
        Booking booking = BookingMapper.toEntity(bookingDto);
        // Mock behavior
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockBooker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(mockItem));
        when(bookingRepository.save(any())).thenReturn(booking);

        // Call the method
        BookingDto result = bookingService.createBookingRequest(bookingDto, mockBooker.getId());

        // Assert
        assertEquals(bookingDto.getStart(), result.getStart());
        assertEquals(bookingDto.getEnd(), result.getEnd());
        // Убедимся, что метод save был вызван один раз с объектом Booking
        verify(bookingRepository, times(1)).save(any(Booking.class));
        // Add more assertions as needed
    }

    @Test
    public void testUpdateBookingApproval() {
        // Arrange
        Long bookingId = 1L;
        Long userId = mockUser.getId();
        boolean approved = true;
        Booking booking = BookingMapper.toEntity(bookingDto);

        User booker = new User();
        booker.setId(userId);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        when(bookingRepository.save(any())).thenReturn(booking);

        // Act
        BookingDto result = bookingService.updateBookingApproval(bookingId, approved, userId);

        // Assert
        assertEquals(approved ? Status.APPROVED : Status.REJECTED, booking.getStatus());
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    public void testGetBooking() {
        Booking booking = BookingMapper.toEntity(bookingDto);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getBooking(booking.getId(), userId);
    }
}