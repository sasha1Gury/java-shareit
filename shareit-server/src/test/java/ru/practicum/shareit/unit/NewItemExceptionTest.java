package ru.practicum.shareit.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.exception.NewItemException;

public class NewItemExceptionTest {

    @Test
    void testExceptionMessage() {
        NewItemException exception = new NewItemException();
        assertEquals("Такая вещь уже существует", exception.getMessage());
    }
}