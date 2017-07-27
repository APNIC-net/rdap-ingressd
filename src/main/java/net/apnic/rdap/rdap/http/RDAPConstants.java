package net.apnic.rdap.rdap.http;

import org.springframework.http.MediaType;

public class RDAPConstants
{
    public static final MediaType RDAP_MEDIA_TYPE =
        new MediaType("application", "rdap+json");

    /**
     * Cannot construct this class
     */
    private RDAPConstants()
    {
    }
}
