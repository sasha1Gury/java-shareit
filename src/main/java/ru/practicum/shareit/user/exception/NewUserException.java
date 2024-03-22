package ru.practicum.shareit.user.exception;

public class NewUserException extends RuntimeException {
    public NewUserException(String massage) {
        super("Пользователь с таким же " + massage + " уже существует");
    }
}
