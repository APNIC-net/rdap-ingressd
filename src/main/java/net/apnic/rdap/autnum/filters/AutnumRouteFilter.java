package net.apnic.rdap.autnum.filters;

import net.apnic.rdap.authority.RDAPAuthority;
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
    public AutnumRouteFilter()
    {
    }

    @Override
    public RDAPAuthority runRDAPFilter(RDAPRequestPath path)
    {
        return null;
    }

    @Override
    public RDAPRequestType supportedRequestType()
    {
        return RDAPRequestType.AUTNUM;
    }
}
