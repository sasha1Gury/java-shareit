package ru.practicum.shareit.dtoTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testBookingDto() throws Exception {
        Item itemDto = Item.builder().id(1L).name("вещь").description("описание вещи").available(true).build();
        User userDto = new User(1L, "John", "john.doe@mail.com");

        LocalDateTime bookingStart = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).minusHours(1);
        LocalDateTime bookingEnd = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(1);
        BookingDto bookingDto = BookingDto.builder().id(1L).item(itemDto).booker(userDto)
                .start(bookingStart).end(bookingEnd).status(Status.APPROVED).build();

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);

        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("вещь");
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo("описание вещи");
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);

        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("John");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("john.doe@mail.com");

        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingStart.format(ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingEnd.format(ISO_LOCAL_DATE_TIME));

        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
    }
}