package net.apnic.rdap.error.controller;

import javax.servlet.http.HttpServletRequest;

import net.apnic.rdap.rdap.RDAPError;
import net.apnic.rdap.rdap.RDAPObjectFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class ErrorController
{
    private ZuulErrorController zuulErrorController;

    @Autowired
    public ErrorController(ZuulErrorController zuulErrorController)
    {
        this.zuulErrorController = zuulErrorController;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RDAPError> handle(HttpServletRequest request)
    {
        String context = request.getRequestURL().toString();
        return zuulErrorController.createErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR, context);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<RDAPError> notFound(HttpServletRequest request)
    {
        String context = request.getRequestURL().toString();
        return zuulErrorController.createErrorResponse(
            HttpStatus.NOT_FOUND, context);
    }
}
