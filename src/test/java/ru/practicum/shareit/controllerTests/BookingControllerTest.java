package ru.practicum.shareit.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.booking.model.Status.*;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private static User booker;
    private static ItemDto item;

    @BeforeAll
    public static void setUp() {
        booker = new User(2, "booker", "booker@booker.ru");
        User owner = new User(1, "Owner", "e@email.com");
        item = ItemDto.builder().id(1).owner(owner).name("вещь1").description("описание 1")
                .available(true).build();
    }

    @Test
    void createBookingEndpointTest() throws Exception {
        when(bookingService.createBookingRequest(any(BookingDto.class), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    BookingDto bookingCreateRequest = invocationOnMock.getArgument(0, BookingDto.class);
                    return new BookingDto(1, bookingCreateRequest.getStart(),
                            bookingCreateRequest.getEnd(), ItemMapper.toEntity(item), booker, WAITING, item.getId());
                });

        BookingDto bookingCreateRequest = BookingDto.builder().itemId(1L)
                .start(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(1))
                .end(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(2)).build();

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingCreateRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("WAITING")));
    }

    @Test
    void approveBookingEndpointTest() throws Exception {
        BookingDto approvedBooking = BookingDto.builder().id(1).item(ItemMapper.toEntity(item)).booker(booker)
                .start(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(1))
                .end(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(1))
                .status(APPROVED).build();

        when(bookingService.updateBookingApproval(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(approvedBooking);

        mvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    void getBookingEndpointTest() throws Exception {
        BookingDto approvedBooking = BookingDto.builder().id(1).item(ItemMapper.toEntity(item)).booker(booker)
                .start(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(1))
                .end(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(1))
                .status(APPROVED).build();

        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(approvedBooking);

        mvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void getBookingsByBookerIdEndpointTest() throws Exception {
        BookingDto approvedBooking = BookingDto.builder().id(1).item(ItemMapper.toEntity(item)).booker(booker)
                .start(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(1))
                .end(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(1))
                .status(APPROVED).build();

        when(bookingService.getUserBookings(any(State.class), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(approvedBooking));

        mvc.perform(get("/bookings?from=0&size=20")
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(approvedBooking))));
    }

    @Test
    void getBookingsByOwnerIdEndpointTest() throws Exception {
        BookingDto approvedBooking = BookingDto.builder().id(1).item(ItemMapper.toEntity(item)).booker(booker)
                .start(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(1))
                .end(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(1))
                .status(APPROVED).build();

        when(bookingService.getOwnerBookings(any(State.class), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(approvedBooking));

        mvc.perform(get("/bookings/owner?from=0&size=20")
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(approvedBooking))));
    }
}
