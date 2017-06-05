package net.apnic.rdap.autnum;

import net.apnic.rdap.authority.RDAPAuthority;
import net.apnic.rdap.resource.ResourceLocator;
import net.apnic.rdap.resource.ResourceNotFoundException;
import net.apnic.rdap.resource.ResourceStore;

import net.ripe.ipresource.etree.IpResourceIntervalStrategy;
import net.ripe.ipresource.etree.NestedIntervalMap;

/**
 * Resource locator for autnum authorities and a store of this information.
 *
 * Class acts as both a resource locator and a store for autnum resources to
 * authority mappings.
 */
public class AutnumStatsResourceLocator
    implements ResourceLocator<AsnRange>, ResourceStore<AsnRange>
{
    private NestedIntervalMap<AsnRange, RDAPAuthority> resources;

    public AutnumStatsResourceLocator()
    {
        IpResourceIntervalStrategy<AsnRange> strategy =
            IpResourceIntervalStrategy.<AsnRange>getInstance();
        resources = new NestedIntervalMap<AsnRange, RDAPAuthority>(strategy);
    }

    /**
     * {@inheritDocs}
     */
    @Override
    public RDAPAuthority authorityForResource(AsnRange range)
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
    public void putResourceMapping(AsnRange resource, RDAPAuthority authority)
    {
        RDAPAuthority estAuthority =
            resources.findExactOrFirstLessSpecific(resource);

        if(estAuthority == null || estAuthority.equals(authority) == false)
        {
            resources.put(resource, authority);
        }
    }
}
