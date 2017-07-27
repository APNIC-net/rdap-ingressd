package net.apnic.rdap.rdap;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Class represents a link object in the RDAP protocl.
 *
 * @see https://tools.ietf.org/html/rfc7483
 */
public class RDAPLink {
    private final String href;
    private final String rel;
    private final String type;
    private final String value;

    public RDAPLink(String href, String rel, String type, String value) {
        this.href = href;
        this.rel = rel;
        this.type = type;
        this.value = value;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getHref()
    {
        return href;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getRel()
    {
        return rel;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getType()
    {
        return type;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getValue()
    {
        return value;
    }

    public RDAPLink withValue(String value) {
        return new RDAPLink(href, rel, type, value);
    }
}
