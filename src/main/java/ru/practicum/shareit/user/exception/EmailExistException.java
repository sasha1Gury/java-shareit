package ru.practicum.shareit.user.exception;

public class EmailExistException extends RuntimeException {
    public EmailExistException(String email) {
        super("Пользователь с таким " + email + " уже существует");
    }
}
