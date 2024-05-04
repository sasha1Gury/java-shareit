package ru.practicum.shareit.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.exception.AvailableNotInitInItem;

public class AvailableNotInitInItemTest {
    @Test
    void testExceptionMessage() {
        AvailableNotInitInItem exception = new AvailableNotInitInItem();
        assertEquals("Available not initialize", exception.getMessage());
    }
}
