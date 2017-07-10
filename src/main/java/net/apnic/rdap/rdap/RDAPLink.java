package net.apnic.rdap.rdap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RDAPLink
{
    private String href;
    private String rel;
    private String type;
    private String value;

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
