package net.apnic.rdap.filter;

import java.util.Arrays;

/**
 * Helper class that takes a request path sent to this RDAP server and provides
 * additional context so further passing can be done.
 */
public class RDAPRequestPath
{
    public static final char PATH_SEPARATOR = '/';

    private String[] pathParts = null;
    private String requestPath = null;
    private String queryString;
    private RDAPRequestType requestType = null;

    /**
     * Constructs a new RDAPRequestPath for a supplied request path.
     *
     * Constructor validates the path against a basic set of constraints that
     * all RDAP path segments must follow.
     *
     * @param requestPath Raw request path recieved for an RDAP path segment
     * @throws IllegalArgumentException If the supplied requestPath does not
     *                                  basically conform to an RDAP path
     *                                  segment
     */
    public RDAPRequestPath(String requestPath)
    {
        if(requestPath == null || requestPath.isEmpty())
        {
            throw new IllegalArgumentException(
                "requestPath cannot be null or empty");
        }

        if(requestPath.charAt(0) == PATH_SEPARATOR)
        {
            requestPath = requestPath.substring(1);
        }

        this.requestPath = requestPath;
        String[] querySplit = requestPath.split("\\?");
        pathParts = querySplit[0].split("/");

        if(pathParts.length < 1)
        {
            throw new IllegalArgumentException(
                "requestPath does not have enough fields");
        }

        this.queryString = querySplit.length > 1 ? querySplit[1] : null;
    }

    /**
     * Returns the request path that this object was constructed with.
     *
     * @return Request path used in this object
     */
    public String getRequestPath()
    {
        return requestPath;
    }

    /**
     * Returns all parameters for this path segment
     *
     * @return Parameters in path segment
     */
    public String[] getRequestParams()
    {
        return Arrays.copyOfRange(pathParts, 1, pathParts.length);
    }

    /**
     * The request type this object represents
     *
     * @return Request type for the supplied path segment
     * @see RDAPRequestType
     */
    public RDAPRequestType getRequestType() {
        if (requestType == null) {
            synchronized (this) {
                if (requestType == null) {
                    requestType = RDAPRequestType.fromPathValue(pathParts[0]);
                }
            }
        }

        return requestType;
    }

    /**
     * Gets the request type value as {@link String} (e.g. "ip", "domain")
     *
     * @return a <pre>String</pre> for the request type value
     */
    public String getRequestTypeValue() {
        return pathParts[0];
    }

    /**
     * Gets the query string for this request as {@link String}.
     * @return the request query string
     */
    public String getQueryString() {
        return queryString;
    }
}
