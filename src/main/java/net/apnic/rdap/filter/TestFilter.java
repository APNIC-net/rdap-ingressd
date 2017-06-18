package net.apnic.rdap.filter;

import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.ZuulFilter;

import java.net.URL;
import java.net.MalformedURLException;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.ROUTE_TYPE;

/**
 *
 */
public class TestFilter
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
        return ROUTE_TYPE;
    }

    @Override
    public boolean shouldFilter()
    {
        return true;
    }

    @Override
    public Object run()
    {
        RequestContext context = RequestContext.getCurrentContext();
        System.out.println("here");
        try
        {
            //context.setRouteHost(new URL("https://rdap.apnic.net/ip/1.46.0.0/15"));
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }
        return null;
    }
}
