package net.apnic.rdap.nameserver.filters;

import net.apnic.rdap.authority.RDAPAuthority;
import net.apnic.rdap.directory.Directory;
import net.apnic.rdap.error.MalformedRequestException;
import net.apnic.rdap.filter.filters.RDAPPathRouteFilter;
import net.apnic.rdap.filter.RDAPRequestPath;
import net.apnic.rdap.filter.RDAPRequestType;
import net.apnic.rdap.nameserver.NameServer;
import net.apnic.rdap.resource.ResourceNotFoundException;

/**
 * Filter for handling nameservers path segments in RDAP requests
 */
public class NameServersRouteFilter
    extends RDAPPathRouteFilter
{
    private static final int NO_REQUEST_PARAMS = 0;

    /**
     * Main constructor which takes the Directory to use for locating
     * nameserver authorities.
     *
     * @param directory
     * @see RDAPPathRouteFilter
     */
    public NameServersRouteFilter(Directory directory)
    {
        super(directory);
    }

    /**
     * Main run method for filter which takes the incoming request and finds a
     * namerserver authority.
     *
     * @see RDAPathRouteFilter
     */
    @Override
    public RDAPAuthority runRDAPFilter(RDAPRequestPath path)
        throws ResourceNotFoundException, MalformedRequestException
    {
        if(path.getRequestParams().length != NO_REQUEST_PARAMS)
        {
            throw new MalformedRequestException(
                "Not enough arguments for nameserver search path segment");
        }

        return getDirectory().getSearchPathAuthority();
    }

    /**
     * @{inheritDocs}
     */
    @Override
    public RDAPRequestType supportedRequestType()
    {
        return RDAPRequestType.NAMESERVERS;
    }
}
