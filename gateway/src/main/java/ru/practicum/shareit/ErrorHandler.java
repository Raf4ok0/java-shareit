package ru.practicum.shareit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.utils.ErrorResponse;


import static ru.practicum.shareit.utils.Constants.UNKNOWN_ERROR_MESSAGE;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(RuntimeException e) {
        log.warn("Произошла ошибка валидации: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(Throwable e) {
        log.error("Произошла неизвестная ошибка: {}", e.getMessage(), e);
        return new ErrorResponse(UNKNOWN_ERROR_MESSAGE);
    }
}
