package net.apnic.rdap.error.rest;

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
    @Value("${error.path:/error}")
    private String errorPath;

    @Override
    public String getErrorPath()
    {
        return errorPath;
    }

    @RequestMapping(value="${error.path:/error}")
    public void error()
    {
    }
}
