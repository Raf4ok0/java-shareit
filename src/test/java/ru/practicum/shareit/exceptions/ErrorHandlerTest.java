package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.ErrorResponse;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class ErrorHandlerTest {

    private final ErrorHandler handler = new ErrorHandler();

    @Test
    void testHandleAlreadyExistException() {
        AlreadyExistException e = new AlreadyExistException("exception");
        ErrorResponse response = handler.handleAlreadyExistException(e);
        assertNotNull(response);
    }


    @Test
    void handleNotFoundException() {
        NotFoundException e = new NotFoundException("exception");
        ErrorResponse response = handler.handleNotFoundRuntimeExceptions(e);
        assertNotNull(response);
    }

    @Test
    void testHandleNotAvailableException() {
        NotAvailableException e = new NotAvailableException("exception");
        ErrorResponse response = handler.handleRuntimeBadRequestExceptions(e);
        assertNotNull(response);
    }


    @Test
    void handleException() {
        Exception e = new Exception("exception");
        ErrorResponse response = handler.handleThrowable(e);
        assertNotNull(response);
    }
}
