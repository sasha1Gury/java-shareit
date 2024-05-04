package ru.practicum.shareit.booking.exception;

public class AvailableException extends RuntimeException {
    public AvailableException() {
        super("Item not available");
    }
}
