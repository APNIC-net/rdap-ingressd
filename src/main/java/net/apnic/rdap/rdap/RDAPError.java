package net.apnic.rdap.rdap;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

public class RDAPError
    extends RDAPObject
{
    private List<String> description = null;
    private String errorCode = null;
    private String title = null;

    public RDAPError addDescription(String description)
    {
        if(this.description == null)
        {
            this.description = new ArrayList<String>();
        }
        this.description.add(description);
        return this;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<String> getDescription()
    {
        return description;
    }

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
