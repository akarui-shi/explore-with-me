package ru.practicum.stats.service.exception;

public class EntityHasNotSavedException extends RuntimeException {

    public EntityHasNotSavedException(String message) {
        super(message);
    }

}