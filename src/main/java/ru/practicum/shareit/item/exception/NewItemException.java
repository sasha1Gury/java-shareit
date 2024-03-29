package ru.practicum.shareit.item.exception;

public class NewItemException extends RuntimeException {
    public NewItemException() {
        super("Такая вещь уже существует");
    }
}
