package net.apnic.rdap.ip;

import net.apnic.rdap.authority.RDAPAuthority;
import net.apnic.rdap.resource.ResourceLocator;
import net.apnic.rdap.resource.ResourceNotFoundException;
import net.apnic.rdap.resource.ResourceStore;

import net.ripe.ipresource.etree.IpResourceIntervalStrategy;
import net.ripe.ipresource.etree.NestedIntervalMap;
import net.ripe.ipresource.IpRange;

/**
 * Resource locator for IP address authorities and a store of this information.
 *
 * Class acts as both a resource locator and a store for IP resources to
 * authority mappings.
 */
public class IPStatsResourceLocator
    implements ResourceLocator<IpRange>, ResourceStore<IpRange>
{
    private NestedIntervalMap<IpRange, RDAPAuthority> resources;

    public IPStatsResourceLocator()
    {
        IpResourceIntervalStrategy<IpRange> strategy =
            IpResourceIntervalStrategy.<IpRange>getInstance();
        resources = new NestedIntervalMap<IpRange, RDAPAuthority>(strategy);
    }

    /**
     * {@inheritDocs}
     */
    @Override
    public RDAPAuthority authorityForResource(IpRange range)
        throws ResourceNotFoundException
    {
        RDAPAuthority authority = resources.findExactOrFirstLessSpecific(range);
        if(authority == null)
        {
            throw new ResourceNotFoundException();
        }
        return authority;
    }

    /**
     * {@inheritDocs}
     */
    @Override
    public synchronized void putResourceMapping(IpRange resource, RDAPAuthority authority)
    {
        RDAPAuthority estAuthority =
            resources.findExactOrFirstLessSpecific(resource);

        if(estAuthority == null || estAuthority.equals(authority) == false)
        {
            resources.put(resource, authority);
        }
    }
}
