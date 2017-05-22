package net.apnic.rdap.client;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

public class RDAPClientErrorHandler
    extends DefaultResponseErrorHandler
{
    @Override
    public boolean hasError(final ClientHttpResponse response)
    {
        return false;
    }
}
