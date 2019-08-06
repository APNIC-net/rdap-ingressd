package net.apnic.rdap.filter.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import net.apnic.rdap.authority.RDAPAuthority;
import net.apnic.rdap.authority.RDAPAuthorityResolver;
import net.apnic.rdap.directory.Directory;
import net.apnic.rdap.error.MalformedRequestException;
import net.apnic.rdap.filter.RDAPRequestPath;
import net.apnic.rdap.filter.RDAPRequestType;
import net.apnic.rdap.filter.config.RequestContextKeys;
import net.apnic.rdap.resource.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_DECORATION_FILTER_ORDER;

/**
 * Zuul pre-filter. It includes in the request the {@link RDAPRequestPath} and {@link RDAPAuthority} to be used by
 * subsequent filters
 */
@Component
public class RDAPPreFilter extends ZuulFilter {

    private static final Logger LOGGER = Logger.getLogger(RDAPPreFilter.class.getName());
    private final Directory directory;
    private final Map<String, RDAPAuthorityResolver> pathToAuthorityFetcherMap;

    @Autowired
    RDAPPreFilter(Directory directory) {
        this.directory = directory;
        pathToAuthorityFetcherMap = initialiseMap();
    }

    @Override
    public int filterOrder() {
        return PRE_DECORATION_FILTER_ORDER - 1;
    }

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();

        LOGGER.info("request: " + context.getRequest().getRequestURI());

        RDAPRequestPath path = new RDAPRequestPath(context.getRequest().getRequestURI());
        context.put(RequestContextKeys.RDAP_REQUEST_PATH.getKey(), path);

        try {
           context.put(RequestContextKeys.RDAP_AUTHORITY.getKey(), getAuthority(path));
        } catch (ResourceNotFoundException | MalformedRequestException e) {
            LOGGER.warning("Error resolving the authority for request: " + e.getMessage());
            throw new RuntimeException(e);  // seems to be the only way to abort the request
        }

        return null;
    }

    private RDAPAuthority getAuthority(RDAPRequestPath path) throws ResourceNotFoundException, MalformedRequestException {
        return pathToAuthorityFetcherMap.get(path.getRequestTypeValue()).resolve(path, directory);
    }

    private Map<String, RDAPAuthorityResolver> initialiseMap() {
        Map<String, RDAPAuthorityResolver> map =
                new HashMap<>(RDAPRequestType.values().length);
        Arrays.stream(RDAPRequestType.values())
                .forEach(t -> map.put(t.getPathValue(), RDAPAuthorityResolver.forType(t)));
        return map;
    }
}
