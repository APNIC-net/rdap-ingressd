package net.apnic.rdap.client;

import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.protocol.HttpContext;

public class RDAPClientRedirectStrategy
    extends DefaultRedirectStrategy
{
    @Override
    public boolean isRedirected(final HttpRequest request,
                                final HttpResponse response,
                                final HttpContext context)
        throws ProtocolException
    {
        return false;
    }
}
