package net.apnic.rdap.error.controller;

import javax.servlet.RequestDispatcher;
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
    private static final String ORIGIN_ATTRIBUTE = "origin";
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

    public ResponseEntity<RDAPError> createErrorResponse(HttpStatus status,
                                                         String context)
    {
        return createErrorResponse(status, null, context);
    }

    public ResponseEntity<RDAPError> createErrorResponse(HttpStatus status,
                                                         String description,
                                                         String context)
    {
        RDAPError error =
            rdapObjectFactory.createRDAPObject(RDAPError.class, context)
                .setErrorCode(status.value())
                .setTitle(status.getReasonPhrase());

        if(description != null)
        {
            error.addDescription(description);
        }

        return new ResponseEntity<RDAPError>(error, responseHeaders,
                                             status);
    }

    @RequestMapping(value="${error.path:/error}")
    public ResponseEntity<RDAPError> error(HttpServletRequest request)
    {
        Throwable topCause = (Throwable)request.getAttribute(EXCEPTION_ATTRIBUTE);
        String context = (String)request.getAttribute(ORIGIN_ATTRIBUTE);

        if(topCause == null)
        {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        System.out.println(topCause.getCause());
        if(causedBy(topCause, MalformedRequestException.class))
        {
            return createErrorResponse(HttpStatus.BAD_REQUEST,
                "Request could not be understood, malformed syntax", context);
        }
        else if(causedBy(topCause, ResourceNotFoundException.class))
        {
            return createErrorResponse(HttpStatus.NOT_FOUND, context);
        }
        else
        {
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, context);
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
