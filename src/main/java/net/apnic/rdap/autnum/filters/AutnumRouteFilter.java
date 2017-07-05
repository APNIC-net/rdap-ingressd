package net.apnic.rdap.autnum.filters;

import net.apnic.rdap.authority.RDAPAuthority;
import net.apnic.rdap.autnum.AsnRange;
import net.apnic.rdap.directory.Directory;
import net.apnic.rdap.error.MalformedRequestException;
import net.apnic.rdap.filter.filters.RDAPPathRouteFilter;
import net.apnic.rdap.filter.RDAPRequestPath;
import net.apnic.rdap.filter.RDAPRequestType;
import net.apnic.rdap.resource.ResourceNotFoundException;

/**
 * Filter for handling autnum path segments in RDAP requests.
 */
public class AutnumRouteFilter
    extends RDAPPathRouteFilter
{
    /**
     * Main constructor which takes the Directory to use for locating autnum
     * authorities.
     *
     * @param directory
     * @see RDAPPathRouteFilter
     */
    public AutnumRouteFilter(Directory directory)
    {
        super(directory);
    }

    /**
     * Main run method for filter which takes the incoming requests and finds
     * the autnum authority.
     *
     * @see RDAPPathRouteFilter
     */
    @Override
    public RDAPAuthority runRDAPFilter(RDAPRequestPath path)
        throws ResourceNotFoundException, MalformedRequestException
    {
        String[] args = path.getRequestParams();

        if(args.length != 1)
        {
            throw new MalformedRequestException(
                "Not enough arguments for autnum path segment");
        }

        try
        {
            return getDirectory().getAutnumAuthority(AsnRange.parse(args[0]));
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
        return RDAPRequestType.AUTNUM;
    }
}
