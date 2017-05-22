package net.apnic.rdap.error.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class ErrorRestHandler
{
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handle(Exception e)
    {
    }

    @ExceptionHandler(value={NoHandlerFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void notFound(Exception e)
    {
    }
}
