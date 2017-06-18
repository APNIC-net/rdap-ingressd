package net.apnic.rdap.ip.filters;

import net.apnic.rdap.authority.RDAPAuthority;
import net.apnic.rdap.filter.filters.RDAPPathRouteFilter;
import net.apnic.rdap.filter.RDAPRequestPath;
import net.apnic.rdap.filter.RDAPRequestType;
import net.apnic.rdap.resource.ResourceLocator;
import net.apnic.rdap.resource.ResourceNotFoundException;

import net.ripe.ipresource.IpRange;

public class IPRouteFilter
    extends RDAPPathRouteFilter
{
    private ResourceLocator<IpRange> ipLocator;

    public IPRouteFilter(ResourceLocator<IpRange> ipLocator)
    {
        this.ipLocator = ipLocator;
    }

    @Override
    public RDAPAuthority runRDAPFilter(RDAPRequestPath path)
    {
        String[] args = path.getRequestParams();
        IpRange range = IpRange.parse(args[0] + "/" + args[1]);

        try
        {
            return ipLocator.authorityForResource(range);
        }
        catch(ResourceNotFoundException ex)
        {
            return null;
        }
    }

    @Override
    public RDAPRequestType supportedRequestType()
    {
        return RDAPRequestType.IP;
    }
}
