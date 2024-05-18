package ru.practicum.ewm.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler({
            EventNotModifiableException.class,
            ConstraintViolationException.class,
            RequestAlreadyExistsException.class,
            NotAuthorizedException.class,
            DataIntegrityViolationException.class
    })
    @ResponseStatus(CONFLICT)
    public ApiError handleConstraintViolationException(Exception e) {
        log.error(e.getLocalizedMessage());
        return ApiError.builder()
                .errors(getStackTraceAsString(e))
                .message(e.getLocalizedMessage())
                .reason("Integrity constraint has been violated.")
                .status(CONFLICT)
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    public ApiError handleNotFoundException(NotFoundDataException exception) {
        log.error(exception.getLocalizedMessage());
        return ApiError.builder()
                .errors(getStackTraceAsString(exception))
                .message(exception.getLocalizedMessage())
                .reason("The required object was not found.")
                .status(NOT_FOUND)
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getLocalizedMessage());
        return ApiError.builder()
                .errors(getStackTraceAsString(e))
                .message("Field: " + Objects.requireNonNull(e.getFieldError()).getField() +
                        ". Error = " + Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage() +
                        " Value: " + e.getFieldError().getRejectedValue())
                .reason("Incorrectly made request.")
                .status(BAD_REQUEST)
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public ApiError handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error(e.getLocalizedMessage());
        return ApiError.builder()
                .errors(getStackTraceAsString(e))
                .message(e.getParameterName() + " is missing.")
                .reason("Missing request parameter " + e.getParameterName())
                .status(BAD_REQUEST)
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleConversionFailedException(MethodArgumentTypeMismatchException e) {
        return ApiError.builder()
                .errors(getStackTraceAsString(e))
                .message("Unknown type: " + e.getValue())
                .reason("Unknown type conversion.")
                .status(BAD_REQUEST)
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public ApiError handleIncorrectDateRangeException(IncorrectDateRangeException e) {
        log.error(e.getLocalizedMessage());
        return ApiError.builder()
                .errors(getStackTraceAsString(e))
                .message(e.getLocalizedMessage())
                .reason("Incorrect date range.")
                .status(BAD_REQUEST)
                .build();
    }

    private String getStackTraceAsString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

}
