package net.apnic.rdap.filter;

import java.util.Arrays;

public class RDAPRequestPath
{
    public static final String PATH_DELIM_REGEX = "\\/";

    private String[] pathParts = null;
    private String requestPath = null;
    private RDAPRequestType requestType = null;
    private boolean valid = true;

    private RDAPRequestPath(String requestPath)
    {
        this.requestPath = requestPath;

        pathParts = requestPath.split(PATH_DELIM_REGEX);

        if(pathParts.length < 1)
        {
            valid = false;
            return;
        }

        setRequestType();
    }

    public static RDAPRequestPath createRequestPath(String requestPath)
    {
        return new RDAPRequestPath(requestPath);
    }

    public String getPath()
    {
        return requestPath;
    }

    public String[] getRequestParams()
    {
        return Arrays.copyOfRange(pathParts, 2, pathParts.length);
    }

    public RDAPRequestType getRequestType()
    {
        return requestType;
    }

    public boolean isValid()
    {
        return valid;
    }

    private void setRequestType()
    {
        try
        {
            requestType = RDAPRequestType.getEnum(pathParts[1]);
        }
        catch(IllegalArgumentException ex)
        {
            valid = false;
        }
    }
}
