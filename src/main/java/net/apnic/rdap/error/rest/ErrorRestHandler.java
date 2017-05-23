package net.apnic.rdap.error.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * Global error handling for Spring rest controllers.
 */
@ControllerAdvice
public class ErrorRestHandler
{
    /**
     * Most non specific error handler to catch all other cases.
     *
     * Errors caught here can be considered as internal server errors.
     *
     * @param error Exception to handle
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handle(Exception error)
    {
    }

    /**
     * Handler for 404 errors.
     *
     * @param error Exception to handle
     */
    @ExceptionHandler(value={NoHandlerFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void notFound(Exception e)
    {
    }
}
