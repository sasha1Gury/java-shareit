package ru.practicum.shareit.unit;

import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.booking.exception.AvailableException;
import ru.practicum.shareit.booking.exception.TimestampException;
import ru.practicum.shareit.booking.exception.UnsupportedStateException;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.ErrorResponse;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.exception.AvailableNotInitInItem;
import ru.practicum.shareit.item.exception.ItemOwnerException;
import ru.practicum.shareit.item.exception.OwnerException;
import ru.practicum.shareit.user.exception.EmailExistException;

import javax.validation.ValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ErrorHandlerTest {

    @Test
    void handleNotFoundException() {
        ErrorHandler errorHandler = new ErrorHandler();
        NotFoundException exception = new NotFoundException("Entity not found");
        ErrorResponse response = errorHandler.handleNotFoundException(exception);
        assertEquals("Объект Entity not found не наден", response.getError());
    }

    @Test
    void handleEmailExistException() {
        ErrorHandler errorHandler = new ErrorHandler();
        EmailExistException exception = new EmailExistException("Email already exists");
        ErrorResponse response = errorHandler.handleEmailExistException(exception);
        assertEquals("Пользователь с таким Email already exists уже существует", response.getError());
    }

    @Test
    void handleUnsupportedStateException() {
        ErrorHandler errorHandler = new ErrorHandler();
        UnsupportedStateException exception = new UnsupportedStateException("Unsupported state");
        ErrorResponse response = errorHandler.handleUnsupportedStateException(exception);
        assertEquals("Unknown state: Unsupported state", response.getError());
    }

    @Test
    void handleOwnerException() {
        ErrorHandler errorHandler = new ErrorHandler();
        OwnerException exception = new OwnerException();
        ErrorResponse response = errorHandler.handleOwnerException(exception);
        assertEquals("Редактировать вещь может только владелец", response.getError());
    }

    @Test
    void handleThrowable() {
        ErrorHandler errorHandler = new ErrorHandler();
        Throwable throwable = new Throwable("Unexpected error");
        ErrorResponse response = errorHandler.handleThrowable(throwable);
        assertEquals("Непредвиденная ошибка Unexpected error", response.getError());
    }

    @Test
    void handleMethodValidationException() {
        ErrorHandler errorHandler = new ErrorHandler();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldError()).thenReturn(new FieldError("objectName", "fieldName", "errorMessage"));
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);
        ErrorResponse response = errorHandler.handleMethodValidationException(exception);
        assertEquals("Ошибка валидации поля 'fieldName': errorMessage", response.getError());
    }

    @Test
    void handleValidationException() {
        ErrorHandler errorHandler = new ErrorHandler();
        ValidationException exception = new ValidationException("Validation error");
        ErrorResponse response = errorHandler.handleValidationException(exception);
        assertEquals("Ошибка валидации поля", response.getError());
    }

    @Test
    void handleAvailableNotInitInItem() {
        ErrorHandler errorHandler = new ErrorHandler();
        AvailableNotInitInItem exception = new AvailableNotInitInItem();
        ErrorResponse response = errorHandler.handleAvailableNotInitInItem(exception);
        assertNotNull(response);
        assertEquals("Available not initialize", response.getError());
    }

    @Test
    void handleAvailableException() {
        ErrorHandler errorHandler = new ErrorHandler();
        AvailableException exception = new AvailableException();
        ErrorResponse response = errorHandler.handleAvailableException(exception);
        assertNotNull(response);
        assertEquals("Item not available", response.getError());
    }

    @Test
    void handleTimestampException() {
        ErrorHandler errorHandler = new ErrorHandler();
        TimestampException exception = new TimestampException();
        ErrorResponse response = errorHandler.handleTimestampException(exception);
        assertNotNull(response);
        assertEquals("Ошибка в указании времени", response.getError());
    }

    @Test
    void handleItemOwnerException() {
        ErrorHandler errorHandler = new ErrorHandler();
        ItemOwnerException exception = new ItemOwnerException();
        ErrorResponse response = errorHandler.handleItemOwnerException(exception);
        assertNotNull(response);
        assertEquals("Оставлять отзывы могут только тот пользователь, который брал эту вещь в аренду", response.getError());
    }
}