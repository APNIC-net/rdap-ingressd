package net.apnic.rdap.filter.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import net.apnic.rdap.authority.RDAPAuthority;
import net.apnic.rdap.authority.routing.RoutingAction;
import net.apnic.rdap.filter.RDAPRequestPath;
import net.apnic.rdap.filter.config.RequestContextKeys;
import net.apnic.rdap.resource.ResourceNotFoundException;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

@Component
public class RDAPPathRouteFilter extends ZuulFilter {

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public String filterType() {
        return FilterConstants.ROUTE_TYPE;
    }

    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();
        RDAPRequestPath path = (RDAPRequestPath) context.get(RequestContextKeys.RDAP_REQUEST_PATH.getKey());

        try {
            RDAPAuthority authority = (RDAPAuthority) context.get(RequestContextKeys.RDAP_AUTHORITY.getKey());
            URI serverURI = authority.getRoutingTarget();

            if (serverURI == null) {
                throw new ResourceNotFoundException();
            }

            if (authority.getRoutingAction() == RoutingAction.REDIRECT) {
                String requestPath = serverURI.resolve(path.getRequestPath()).toString();
                RDAPAuthority fallbackAuthority = authority.getNotFoundFallback();

                if (fallbackAuthority != null) {
                    // check if server won't return 404
                    HttpURLConnection connection = (HttpURLConnection) new URL(requestPath).openConnection();
                    connection.setRequestMethod("HEAD");
                    connection.setInstanceFollowRedirects(false);

                    if (connection.getResponseCode() == HttpStatus.NOT_FOUND.value()) {
                        if (fallbackAuthority.getRoutingAction() == RoutingAction.PROXY) {
                            context.setRouteHost(fallbackAuthority.getRoutingTarget().toURL());
                            return null;
                        } else {
                            requestPath =
                                    fallbackAuthority.getRoutingTarget().resolve(path.getRequestPath()).toString();
                        }
                    }
                }

                context.unset();
                context.getResponse().setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
                context.getResponse().setHeader("Location", requestPath);
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


}
