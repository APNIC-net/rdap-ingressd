package net.apnic.rdap.filter.filters;

import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.ZuulFilter;

import net.apnic.rdap.filter.config.RequestContextKeys;
import net.apnic.rdap.filter.RDAPRequestPath;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

/**
 *
 */
public class RDAPPreFilter
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

        RDAPRequestPath path = RDAPRequestPath.createRequestPath(
                context.getRequest().getRequestURI());
        context.put(RequestContextKeys.RDAP_REQUEST_PATH, path);

        // Place the origin url into the request attributes.
        // We do this because Zuul and spring may route to other endpoints and
        // this information will be lost.
        // Information is needed to contextualise RDAP responses
        context.getRequest().setAttribute(RequestContextKeys.RDAP_ORIGIN_URL,
            context.getRequest().getRequestURL().toString());

        return null;
    }
}
