package net.apnic.rdap.error.controller;

import javax.servlet.http.HttpServletRequest;

import net.apnic.rdap.error.MalformedRequestException;
import net.apnic.rdap.rdap.RDAPError;
import net.apnic.rdap.rdap.RDAPObjectFactory;
import net.apnic.rdap.resource.ResourceNotFoundException;

import org.apache.commons.lang3.exception.ExceptionUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ZuulErrorController
    implements ErrorController
{
    private static final String EXCEPTION_ATTRIBUTE =
        "javax.servlet.error.exception";

    private static final MediaType RDAP_MEDIA_TYPE =
        new MediaType("application", "rdap+json");

    @Value("${error.path:/error}")
    private String errorPath;

    private RDAPObjectFactory rdapObjectFactory = null;
    private HttpHeaders responseHeaders = null;

    @Autowired
    public ZuulErrorController(RDAPObjectFactory rdapObjectFactory)
    {
        this.rdapObjectFactory = rdapObjectFactory;
        setupResponseHeaders();
    }

    private boolean causedBy(Throwable th, Class<?> clazz)
    {
        return ExceptionUtils.indexOfThrowable(th, clazz) != -1;
    }

    private ResponseEntity<RDAPError> createErrorResponse(HttpStatus status)
    {
        return new ResponseEntity<RDAPError>(
            rdapObjectFactory.createRDAPObject(RDAPError.class)
                .setErrorCode(status.value())
                .setTitle(status.getReasonPhrase()),
            responseHeaders,
            status);
    }

    @RequestMapping(value="${error.path:/error}")
    public ResponseEntity<RDAPError> error(HttpServletRequest request)
    {
        Throwable topCause = (Throwable)request.getAttribute(EXCEPTION_ATTRIBUTE);
        if(topCause == null)
        {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(causedBy(topCause, MalformedRequestException.class))
        {
            return createErrorResponse(HttpStatus.BAD_REQUEST);
        }
        else if(causedBy(topCause, ResourceNotFoundException.class))
        {
            return null; // 404 RDAP Response
        }
        else
        {
            return null; // 500 RDAP Response
        }
    }

    @Override
    public String getErrorPath()
    {
        return errorPath;
    }

    private void setupResponseHeaders()
    {
        responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(RDAP_MEDIA_TYPE);
    }
}
