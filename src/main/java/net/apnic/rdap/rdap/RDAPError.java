package net.apnic.rdap.rdap;

import com.fasterxml.jackson.annotation.JsonInclude;

public class RDAPError
    extends RDAPObject
{
    private String errorCode;
    private String title;

    public String getErrorCode()
    {
        return errorCode;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getTitle()
    {
        return title;
    }

    public RDAPError setErrorCode(int errorCode)
    {
        this.errorCode = Integer.toString(errorCode);
        return this;
    }

    public RDAPError setTitle(String title)
    {
        this.title = title;
        return this;
    }
}
