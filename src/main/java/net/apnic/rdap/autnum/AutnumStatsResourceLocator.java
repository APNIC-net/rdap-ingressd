package net.apnic.rdap.autnum;

import net.apnic.rdap.authority.RDAPAuthority;
import net.apnic.rdap.resource.ResourceLocator;
import net.apnic.rdap.resource.ResourceNotFoundException;

import net.ripe.ipresource.etree.IpResourceIntervalStrategy;
import net.ripe.ipresource.etree.NestedIntervalMap;

public class AutnumStatsResourceLocator
    implements ResourceLocator<AsnRange>
{
    private NestedIntervalMap<AsnRange, RDAPAuthority> resources;

    public AutnumStatsResourceLocator()
    {
        IpResourceIntervalStrategy<AsnRange> strategy =
            IpResourceIntervalStrategy.<AsnRange>getInstance();
        resources = new NestedIntervalMap<AsnRange, RDAPAuthority>(strategy);
    }

    @Override
    public RDAPAuthority authorityForResource(AsnRange autnum)
        throws ResourceNotFoundException
    {
        throw new ResourceNotFoundException();
    }
}
