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
 * Filter for handling nameserver path segments in RDAP requests
 */
public class NameServerRouteFilter
    extends RDAPPathRouteFilter
{
    private static final int NS_PARAM_INDEX = 0;
    private static final int NO_REQUEST_PARAMS = 1;

    /**
     * Main constructor which takes the Directory to use for locating
     * nameserver authorities.
     *
     * @param directory
     * @see RDAPPathRouteFilter
     */
    public NameServerRouteFilter(Directory directory)
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
        String[] args = path.getRequestParams();

        if(args.length != NO_REQUEST_PARAMS)
        {
            throw new MalformedRequestException(
                "Not enough arguments for domain path segment");
        }

        return getDirectory()
            .getNameServerAuthority(new NameServer(args[NS_PARAM_INDEX]));
    }

    /**
     * @{inheritDocs}
     */
    @Override
    public RDAPRequestType supportedRequestType()
    {
        return RDAPRequestType.NAMESERVER;
    }
}
