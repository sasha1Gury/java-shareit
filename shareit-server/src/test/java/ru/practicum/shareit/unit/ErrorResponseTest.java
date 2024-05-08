package ru.practicum.shareit.unit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ErrorResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorResponseTest {

    @Test
    void testGetError() {
        // Создание объекта ErrorResponse
        ErrorResponse errorResponse = new ErrorResponse("Test error message");

        // Проверка, что геттер возвращает правильное значение ошибки
        assertEquals("Test error message", errorResponse.getError());
    }
}
