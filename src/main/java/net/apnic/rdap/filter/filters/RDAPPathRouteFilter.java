package net.apnic.rdap.filter.filters;

import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.ZuulFilter;

import java.net.URI;

import net.apnic.rdap.authority.RDAPAuthority;
import net.apnic.rdap.error.MalformedRequestException;
import net.apnic.rdap.filter.config.RequestContextKeys;
import net.apnic.rdap.filter.RDAPRequestPath;
import net.apnic.rdap.filter.RDAPRequestType;
import net.apnic.rdap.resource.ResourceNotFoundException;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

/**
 *
 */
public abstract class RDAPPathRouteFilter
    extends ZuulFilter
{
    @Override
    public int filterOrder()
    {
        return 1;
    }

    @Override
    public String filterType()
    {
        return FilterConstants.ROUTE_TYPE;
    }

    public boolean shouldFilter()
    {
        RequestContext context = RequestContext.getCurrentContext();
        RDAPRequestPath path =
            (RDAPRequestPath)context.get(RequestContextKeys.RDAP_REQUEST_PATH);

        return supportedRequestType() == path.getRequestType();
    }

    @Override
    public Object run()
    {
        RequestContext context = RequestContext.getCurrentContext();
        RDAPRequestPath path =
            (RDAPRequestPath)context.get(RequestContextKeys.RDAP_REQUEST_PATH);

        try
        {
            RDAPAuthority authority = runRDAPFilter(path);

            URI serverURI = authority.getDefaultServerURI();
            if(serverURI == null)
            {
                throw new ResourceNotFoundException();
            }
            context.setRouteHost(serverURI.toURL());
        }
        catch(ResourceNotFoundException ex)
        {
            //TODO
        }
        catch(Exception ex)
        {
            throw new RuntimeException(ex);
        }

        return null;
    }

    public abstract RDAPAuthority runRDAPFilter(RDAPRequestPath path)
        throws ResourceNotFoundException, MalformedRequestException;

    public abstract RDAPRequestType supportedRequestType();
}
