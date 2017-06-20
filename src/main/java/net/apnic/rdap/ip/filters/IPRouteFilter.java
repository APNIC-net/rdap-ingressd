package net.apnic.rdap.ip.filters;

import net.apnic.rdap.authority.RDAPAuthority;
import net.apnic.rdap.error.MalformedRequestException;
import net.apnic.rdap.filter.filters.RDAPPathRouteFilter;
import net.apnic.rdap.filter.RDAPRequestPath;
import net.apnic.rdap.filter.RDAPRequestType;
import net.apnic.rdap.resource.ResourceLocator;
import net.apnic.rdap.resource.ResourceNotFoundException;

import net.ripe.ipresource.IpAddress;
import net.ripe.ipresource.IpRange;
import net.ripe.ipresource.IpResourceType;

/**
 *
 */
public class IPRouteFilter
    extends RDAPPathRouteFilter
{
    private ResourceLocator<IpRange> ipLocator;

    public IPRouteFilter(ResourceLocator<IpRange> ipLocator)
    {
        super(null);
        this.ipLocator = ipLocator;
    }

    @Override
    public RDAPAuthority runRDAPFilter(RDAPRequestPath path)
        throws ResourceNotFoundException, MalformedRequestException
    {
        String[] args = path.getRequestParams();

        if(args.length == 0 || args.length > 2)
        {
            throw new MalformedRequestException(
                "Not enough arguments for ip path segment");
        }

        try
        {
            IpAddress address = IpAddress.parse(args[0]);
            int prefixLength = address.getType() == IpResourceType.IPv4 ?
                IpResourceType.IPv4.getBitSize() :
                IpResourceType.IPv6.getBitSize();

            if(args.length == 2)
            {
                prefixLength = Integer.parseInt(args[1]);
            }

            return ipLocator.authorityForResource(
                IpRange.prefix(address, prefixLength));
        }
        catch(NumberFormatException ex)
        {
            throw new MalformedRequestException(ex);
        }
        catch(IllegalArgumentException ex)
        {
            throw new MalformedRequestException(ex);
        }
    }

    @Override
    public RDAPRequestType supportedRequestType()
    {
        return RDAPRequestType.IP;
    }
}
