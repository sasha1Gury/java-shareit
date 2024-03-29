package ru.practicum.shareit.item.exception;

public class AvailableNotInitInItem extends RuntimeException {
    public AvailableNotInitInItem() {
        super("Available not initialize");
    }
}
