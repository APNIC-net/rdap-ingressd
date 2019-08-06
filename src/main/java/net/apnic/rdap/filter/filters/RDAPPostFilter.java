package net.apnic.rdap.filter.filters;

import com.netflix.util.Pair;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import net.apnic.rdap.authority.RDAPAuthority;
import net.apnic.rdap.authority.routing.RoutingAction;
import net.apnic.rdap.filter.RDAPRequestPath;
import net.apnic.rdap.filter.config.RequestContextKeys;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.List;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.SEND_RESPONSE_FILTER_ORDER;

@Component
public class RDAPPostFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return SEND_RESPONSE_FILTER_ORDER - 1;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext context = RequestContext.getCurrentContext();
        RDAPAuthority rdapAuthority = (RDAPAuthority) context.get(RequestContextKeys.RDAP_AUTHORITY.getKey());
        return rdapAuthority != null
                && rdapAuthority.getNotFoundFallback() != null
                && rdapAuthority.getRoutingAction() == RoutingAction.PROXY;
    }

    @Override
    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletResponse response = context.getResponse();

        if (response != null && response.getStatus() == HttpStatus.NOT_FOUND.value()) {
            RDAPRequestPath path = (RDAPRequestPath) context.get(RequestContextKeys.RDAP_REQUEST_PATH.getKey());
            RDAPAuthority rdapAuthority = (RDAPAuthority) context.get(RequestContextKeys.RDAP_AUTHORITY.getKey());
            RDAPAuthority fallbackAuthority = rdapAuthority.getNotFoundFallback();

            URI fallbackPath = fallbackAuthority.getRoutingTarget().resolve(path.getRequestPath());
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> fallbackResponse;

            try {
                fallbackResponse = restTemplate.exchange(fallbackPath, HttpMethod.GET, null, String.class);
            } catch (RestClientResponseException e) {
                fallbackResponse = new ResponseEntity<>(e.getResponseBodyAsString(),
                                                        e.getResponseHeaders(),
                                                        HttpStatus.valueOf(e.getRawStatusCode()));
            }

            // replace headers
            List<Pair<String, String>> zuulResponseHeaders = context.getZuulResponseHeaders();
            zuulResponseHeaders.clear();
            fallbackResponse.getHeaders().forEach(
                    (key, values) -> values.forEach(v -> zuulResponseHeaders.add(new Pair<>(key, v))));

            context.setResponseStatusCode(fallbackResponse.getStatusCodeValue());
            context.setResponseBody(fallbackResponse.getBody());
        }

        return null;
    }
}
