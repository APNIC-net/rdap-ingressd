package net.apnic.rdap.error.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.ZuulFilter;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import net.apnic.rdap.error.MalformedRequestException;
import net.apnic.rdap.rdap.http.RDAPConstants;
import net.apnic.rdap.rdap.RDAPError;
import net.apnic.rdap.rdap.RDAPObjectFactory;
import net.apnic.rdap.resource.ResourceNotFoundException;

import org.apache.commons.lang.exception.ExceptionUtils;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;

/**
 * Zuul filter for handling errors.
 */
public class ZuulErrorFilter
    extends ZuulFilter
{
    private static final String SEND_ERROR_FILTER_RAN = "sendErrorFilter.ran";

    private RDAPObjectFactory rdapObjectFactory = null;

    public ZuulErrorFilter(RDAPObjectFactory rdapObjectFactory)
    {
        this.rdapObjectFactory = rdapObjectFactory;
    }

    private boolean causedBy(Throwable th, Class<?> clazz)
    {
        return ExceptionUtils.indexOfThrowable(th, clazz) != -1;
    }

    /**
     * {@inheritDocs}
     */
    @Override
    public String filterType()
    {
        return FilterConstants.ERROR_TYPE;
    }

    /**
     * {@inheritDocs}
     */
    @Override
    public int filterOrder()
    {
        return -1;
    }

    /**
     * {@inheritDocs}
     */
    @Override
    public boolean shouldFilter()
    {
        RequestContext context = RequestContext.getCurrentContext();
        return context.getThrowable() != null
            && context.getBoolean(SEND_ERROR_FILTER_RAN, false) == false;
    }

    /**
     * {@inheritDocs}
     */
    @Override
    public Object run()
    {
        RequestContext context = RequestContext.getCurrentContext();
        Throwable throwable = context.getThrowable();

        if(causedBy(throwable, MalformedRequestException.class))
        {
            sendErrorResponse(HttpStatus.BAD_REQUEST,
                "Request could not be understood, malformed syntax", context);
        }
        else if(causedBy(throwable, ResourceNotFoundException.class))
        {
            sendErrorResponse(HttpStatus.NOT_FOUND, null, context);
        }
        else
        {
            sendErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, context);
        }

        context.set(SEND_ERROR_FILTER_RAN, true);
        return null;
    }

    /**
     * Constructs and sends a valid RDAP error response for a given http status
     * code.
     */
    private void sendErrorResponse(HttpStatus status, String description,
                                   RequestContext context)
    {
        String originURL = context.getRequest().getRequestURL().toString();
        List<String> descriptions = Optional.ofNullable(description).map(Collections::singletonList).orElse(Collections.emptyList());

        RDAPError error =
            rdapObjectFactory.createErrorObject(originURL, descriptions,
                                                status.toString(),
                                                status.getReasonPhrase());

        context.setSendZuulResponse(false);
        context.setResponseStatusCode(status.value());
        context.getResponse().setContentType(
            RDAPConstants.RDAP_MEDIA_TYPE.toString());

        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(context.getResponse().getOutputStream(), error);
        }
        catch(Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }
}
