package net.apnic.rdap.rdap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class represents a link object in the RDAP protocl.
 *
 * @see https://tools.ietf.org/html/rfc7483
 */
public class RDAPLink
    implements Cloneable
{
    private String href = null;
    private String rel = null;
    private String type = null;
    private String value = null;

    /**
     * {@inheritDocs}
     */
    public RDAPLink clone()
    {
        RDAPLink link = new RDAPLink();
        link.setHref(getHref());
        link.setRel(getRel());
        link.setType(getType());
        link.setValue(getValue());
        return link;
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

    public RDAPLink setHref(String href)
    {
        this.href = href;
        return this;
    }

    public RDAPLink setRel(String rel)
    {
        this.rel = rel;
        return this;
    }

    public RDAPLink setType(String type)
    {
        this.type = type;
        return this;
    }

    public RDAPLink setValue(String value)
    {
        this.value = value;
        return this;
    }
}
