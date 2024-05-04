package ru.practicum.shareit.item.exception;

public class ItemOwnerException extends RuntimeException {
    public ItemOwnerException() {
        super("Оставлять отзывы могут только тот пользователь, который брал эту вещь в аренду");
    }
}
