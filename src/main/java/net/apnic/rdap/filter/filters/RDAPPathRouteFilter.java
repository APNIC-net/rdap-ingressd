package net.apnic.rdap.filter.filters;

import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.ZuulFilter;

import java.net.URI;
import java.net.URL;

import net.apnic.rdap.authority.RDAPAuthority;
import net.apnic.rdap.authority.routing.RoutingAction;
import net.apnic.rdap.directory.Directory;
import net.apnic.rdap.error.MalformedRequestException;
import net.apnic.rdap.filter.config.RequestContextKeys;
import net.apnic.rdap.filter.RDAPRequestPath;
import net.apnic.rdap.filter.RDAPRequestType;
import net.apnic.rdap.resource.ResourceNotFoundException;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

public abstract class RDAPPathRouteFilter
    extends ZuulFilter
{
    private Directory directory = null;

    public RDAPPathRouteFilter(Directory directory)
    {
        this.directory = directory;
    }

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

    public Directory getDirectory()
    {
        return directory;
    }

    public RDAPRequestPath getRDAPRequestPath()
    {
        RequestContext context = RequestContext.getCurrentContext();
        return (RDAPRequestPath)context.get(RequestContextKeys.RDAP_REQUEST_PATH);
    }

    public boolean shouldFilter()
    {
        RDAPRequestPath path = getRDAPRequestPath();
        if(path == null)
        {
            return false;
        }
        return supportedRequestType() == path.getRequestType();
    }

    @Override
    public Object run()
    {
        RequestContext context = RequestContext.getCurrentContext();
        RDAPRequestPath path = getRDAPRequestPath();

        try
        {
            RDAPAuthority authority = runRDAPFilter(path);

            URI serverURI = authority.getDefaultServerURI();
            if(serverURI == null)
            {
                throw new ResourceNotFoundException();
            }

            if(authority.getRoutingAction() == RoutingAction.REDIRECT)
            {
                context.unset();
                context.getResponse().sendRedirect(serverURI.resolve(path.getRequestPath()).toString());
            }
            else
            {
                context.setRouteHost(serverURI.toURL());
            }
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
