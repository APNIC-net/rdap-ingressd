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
 * Filter for handling domain path segments in RDAP requests
 */
public class DomainRouteFilter
    extends RDAPPathRouteFilter
{
    private static final int DOMAIN_PARAM_INDEX = 0;
    private static final int NO_REQUEST_PARAMS = 1;

    /**
     * Main constructor which takes the Directory to use for locating domain
     * authorities.
     *
     * @param directory
     * @see RDAPPathRouteFilter
     */
    public DomainRouteFilter(Directory directory)
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
        String[] args = path.getRequestParams();

        if(args.length != NO_REQUEST_PARAMS)
        {
            throw new MalformedRequestException(
                "Not enough arguments for domain path segment");
        }

        try
        {
            return getDirectory()
                .getDomainAuthority(new Domain(args[DOMAIN_PARAM_INDEX]));
        }
        catch(IllegalArgumentException ex)
        {
            throw new MalformedRequestException(ex);
        }
    }

    /**
     * {@inheritDocs}
     */
    @Override
    public RDAPRequestType supportedRequestType()
    {
        return RDAPRequestType.DOMAIN;
    }
}
