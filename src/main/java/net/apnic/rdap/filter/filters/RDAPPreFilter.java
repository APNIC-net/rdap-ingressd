package net.apnic.rdap.filter.filters;

import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.ZuulFilter;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.apnic.rdap.filter.config.RequestContextKeys;
import net.apnic.rdap.filter.RDAPRequestPath;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

/**
 *
 */
public class RDAPPreFilter
    extends ZuulFilter
{
    private final Logger LOGGER =
        Logger.getLogger(RDAPPreFilter.class.getName());

    @Override
    public int filterOrder()
    {
        return 1;
    }

    @Override
    public String filterType()
    {
        return FilterConstants.PRE_TYPE;
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

        LOGGER.info("request: " + context.getRequest().getRequestURI());

        RDAPRequestPath path = RDAPRequestPath.createRequestPath(
                context.getRequest().getRequestURI());
        context.put(RequestContextKeys.RDAP_REQUEST_PATH, path);

        // TODO: ADD AUTHORITY HERE
        return null;
    }
}
