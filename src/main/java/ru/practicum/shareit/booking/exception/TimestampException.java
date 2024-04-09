package ru.practicum.shareit.booking.exception;

public class TimestampException extends RuntimeException {
    public TimestampException() {
        super("Ошибка в указании времени");
    }
}
