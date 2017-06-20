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
 *
 */
public class AutnumRouteFilter
    extends RDAPPathRouteFilter
{
    public AutnumRouteFilter(Directory directory)
    {
        super(directory);
    }

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

    @Override
    public RDAPRequestType supportedRequestType()
    {
        return RDAPRequestType.AUTNUM;
    }
}
