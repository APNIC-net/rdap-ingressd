package net.apnic.rdap.error.controller;

import javax.servlet.http.HttpServletRequest;

import net.apnic.rdap.error.MalformedRequestException;
import net.apnic.rdap.resource.ResourceNotFoundException;

import org.apache.commons.lang3.exception.ExceptionUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ZuulErrorController
    implements ErrorController
{
    private static final String EXCEPTION_ATTRIBUTE =
        "javax.servlet.error.exception";

    @Value("${error.path:/error}")
    private String errorPath;

    private boolean causedBy(Throwable th, Class<?> clazz)
    {
        return ExceptionUtils.indexOfThrowable(th, clazz) == -1;
    }

    @Override
    public String getErrorPath()
    {
        return errorPath;
    }

    @RequestMapping(value="${error.path:/error}")
    public void error(HttpServletRequest request)
    {
        Throwable topCause = (Throwable)request.getAttribute(EXCEPTION_ATTRIBUTE);
        if(topCause == null)
        {
            return;
        }

        if(causedBy(topCause, MalformedRequestException.class))
        {
            return; // 400 RDAP Response
        }
        else if(causedBy(topCause, ResourceNotFoundException.class))
        {
            return; // 404 RDAP Response
        }
        else
        {
            return; // 500 RDAP Response
        }
    }
}
