package ru.practicum.shareit.item.exception;

public class OwnerException extends RuntimeException {
    public OwnerException() {
        super("Редактировать вещь может только владелец");
    }
}
