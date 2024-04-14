package ru.practicum.shareit.booking.exception;

public class UnsupportedStateException extends RuntimeException {
    public UnsupportedStateException(String message) {
        super("Unknown state: " + message);
    }
}
