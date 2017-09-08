package net.apnic.rdap.domain.filters;

import net.apnic.rdap.authority.RDAPAuthority;
import net.apnic.rdap.directory.Directory;
import net.apnic.rdap.domain.Domain;
import net.apnic.rdap.error.MalformedRequestException;
import net.apnic.rdap.filter.filters.RDAPPathRouteFilter;
import net.apnic.rdap.filter.RDAPRequestPath;
import net.apnic.rdap.filter.RDAPRequestType;
import net.apnic.rdap.resource.ResourceNotFoundException;

/**
 * Filter for handling domain search path segments in RDAP requests
 */
public class DomainsRouteFilter
    extends RDAPPathRouteFilter
{
    private static final int NO_REQUEST_PARAMS = 0;

    /**
     * Main constructor which takes the Directory to use for locating domain
     * authorities.
     *
     * @param directory
     * @see RDAPPathRouteFilter
     */
    public DomainsRouteFilter(Directory directory)
    {
        super(directory);
    }

    /**
     * Main run method for filter which takes the incoming request and
     * finds a domain authority.
     *
     * @see RDAPPathRouteFilter
     */
    @Override
    public RDAPAuthority runRDAPFilter(RDAPRequestPath path)
        throws ResourceNotFoundException, MalformedRequestException
    {
        if(path.getRequestParams().length != NO_REQUEST_PARAMS)
        {
            throw new MalformedRequestException(
                "Not enough arguments for domain search path segment");
        }

        return getDirectory().getSearchPathAuthority();
    }

    /**
     * {@inheritDocs}
     */
    @Override
    public RDAPRequestType supportedRequestType()
    {
        return RDAPRequestType.DOMAINS;
    }
}
