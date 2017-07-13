package net.apnic.rdap.error.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import net.apnic.rdap.error.MalformedRequestException;
import net.apnic.rdap.filter.config.RequestContextKeys;
import net.apnic.rdap.rdap.RDAPError;
import net.apnic.rdap.rdap.RDAPObjectFactory;
import net.apnic.rdap.resource.ResourceNotFoundException;

import org.apache.commons.lang3.exception.ExceptionUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Error controller for Zuul and spring that responds to requests on the error
 * path.
 *
 * This is used in conjunction with the error controller. Unfotunetly to the way
 * errors are handled in Zuul any exception thrown is redirected to the error
 * path.
 *
 * Its the job of this class to take the exception and work out the most
 * appropriate RDAP response.
 */
@ControllerAdvice
@RestController
public class ErrorController
    implements org.springframework.boot.autoconfigure.web.ErrorController
{
    private static final String EXCEPTION_ATTRIBUTE =
        "javax.servlet.error.exception";
    private static final MediaType RDAP_MEDIA_TYPE =
        new MediaType("application", "rdap+json");

    @Value("${error.path:/error}")
    private String errorPath;

    private RDAPObjectFactory rdapObjectFactory = null;
    private HttpHeaders responseHeaders = null;

    /**
     * Main constructor which takes the services RDAPObjectFactory to construct
     * RDAPError's from.
     *
     * @param rdapObjectFactory Object factory to construct RDAPError objects
     *                          from
     */
    @Autowired
    public ErrorController(RDAPObjectFactory rdapObjectFactory)
    {
        this.rdapObjectFactory = rdapObjectFactory;
        setupResponseHeaders();
    }

    /**
     * Internal util function for checking if a throwable was caused by the
     * provided class.
     *
     * Rudimentary method for unraveling the wrapping done by Zuul.
     *
     * @param th Throwable to check
     * @param clazz Class to check is contained within the the throwable.
     */
    private boolean causedBy(Throwable th, Class<?> clazz)
    {
        return ExceptionUtils.indexOfThrowable(th, clazz) != -1;
    }

    /**
     * Constructs a new RDAPError object for the provided HttpStatus and
     * context.
     *
     * @param status HttpStatus for the RDAPError
     * @param context The context the error request was made in. This will be
     *                the request URL in most cases
     */
    private ResponseEntity<RDAPError> createErrorResponse(HttpStatus status,
                                                         String context)
    {
        return createErrorResponse(status, null, context);
    }

    /**
     * Constructs a new RDAPError object for the provided HttpStatus
     * description, and context.
     *
     * @param status HttpStatus for the RDAPError
     * @param description Description for the error object
     * @param context The context the error request was made in. This will be
     *                the request URL in most cases
     */
    private ResponseEntity<RDAPError> createErrorResponse(HttpStatus status,
                                                         String description,
                                                         String context)
    {
        List<String> descriptions = Optional.ofNullable(description).map(Collections::singletonList).orElse(Collections.emptyList());
        String errorCode = "" + status.value();
        String title = status.getReasonPhrase();
        RDAPError error = rdapObjectFactory.createRDAPObject(
                    RDAPError.class, context, descriptions, errorCode, title);

        return new ResponseEntity<RDAPError>(error, responseHeaders,
                                             status);
    }

    /**
     * Spring error route handler.
     *
     * Function attempts to work out the cause of the error and provide a
     * correct RDAPError resonse. If no specific cause can be worked out then
     * a default internal server error is returned.
     */
    @RequestMapping(value="${error.path:/error}")
    public ResponseEntity<RDAPError> error(HttpServletRequest request)
    {
        Throwable topCause = (Throwable)request.getAttribute(EXCEPTION_ATTRIBUTE);
        String context = (String)request.getAttribute(RequestContextKeys.RDAP_ORIGIN_URL);

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
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, context);
    }

    /**
     * Provides the configured error path for Spring.
     */
    @Override
    public String getErrorPath()
    {
        return errorPath;
    }

    /**
     * Generic spring handler for all exceptions that occur outside of Zuul
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RDAPError> handleException(HttpServletRequest request)
    {
        String context = request.getRequestURL().toString();
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, context);
    }

    /**
     * Handles all Spring exceptions outside of Zuul where no rest handler can
     * be found. These are always 404 errors.
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<RDAPError> notFound(HttpServletRequest request)
    {
        String context = request.getRequestURL().toString();
        return createErrorResponse(HttpStatus.NOT_FOUND, context);
    }

    private void setupResponseHeaders()
    {
        responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(RDAP_MEDIA_TYPE);
    }
}
