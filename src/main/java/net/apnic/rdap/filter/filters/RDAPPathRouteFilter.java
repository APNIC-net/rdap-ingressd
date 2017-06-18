package net.apnic.rdap.filter.filters;

import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.ZuulFilter;

import java.net.URL;

import net.apnic.rdap.authority.RDAPAuthority;
import net.apnic.rdap.filter.config.RequestContextKeys;
import net.apnic.rdap.filter.RDAPRequestPath;
import net.apnic.rdap.filter.RDAPRequestType;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

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

        RDAPAuthority authority = runRDAPFilter(path);

        try
        {
            context.setRouteHost(authority.getServers().get(0).toURL());
        }
        catch(Exception ex)
        {
            System.out.println("Exception caught");
        }

        return null;
    }

    public abstract RDAPAuthority runRDAPFilter(RDAPRequestPath path);

    public abstract RDAPRequestType supportedRequestType();
}
