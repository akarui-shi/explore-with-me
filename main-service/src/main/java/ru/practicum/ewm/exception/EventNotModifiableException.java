package ru.practicum.ewm.exception;

public class EventNotModifiableException extends RuntimeException {
    public EventNotModifiableException(String message) {
        super(message);
    }
}
