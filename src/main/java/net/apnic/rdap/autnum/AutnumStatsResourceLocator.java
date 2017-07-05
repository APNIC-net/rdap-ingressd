package net.apnic.rdap.autnum;

import java.util.concurrent.atomic.AtomicReference;

import net.apnic.rdap.authority.RDAPAuthority;
import net.apnic.rdap.resource.ResourceLocator;
import net.apnic.rdap.resource.ResourceNotFoundException;
import net.apnic.rdap.resource.store.ResourceStorage;

import net.ripe.ipresource.etree.IpResourceIntervalStrategy;
import net.ripe.ipresource.etree.NestedIntervalMap;

/**
 * Resource locator for autnum authorities and a store of this information.
 *
 * Class acts as both a resource locator and a store for autnum resources to
 * authority mappings.
 */
public class AutnumStatsResourceLocator
    implements ResourceLocator<AsnRange>, ResourceStorage<AsnRange>
{
    private AtomicReference<NestedIntervalMap<AsnRange, RDAPAuthority>> resources;

    public AutnumStatsResourceLocator()
    {
        IpResourceIntervalStrategy<AsnRange> strategy =
            IpResourceIntervalStrategy.<AsnRange>getInstance();
        resources =
            new AtomicReference<NestedIntervalMap<AsnRange, RDAPAuthority>>(
                new NestedIntervalMap<AsnRange, RDAPAuthority>(strategy));
    }

    /**
     * {@inheritDocs}
     */
    @Override
    public RDAPAuthority authorityForResource(AsnRange range)
        throws ResourceNotFoundException
    {
        RDAPAuthority authority =
            resources.get().findExactOrFirstLessSpecific(range);
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
    public ResourceStorage<AsnRange> initialiseNew()
    {
        return new AutnumStatsResourceLocator();
    }

    /**
     * {@inheritDocs}
     */
    @Override
    public void moveStorage(ResourceStorage<AsnRange> asnStorage)
    {
        if(getClass() != asnStorage.getClass())
        {
            return;
        }

        AutnumStatsResourceLocator newLocator =
            (AutnumStatsResourceLocator)asnStorage;

        this.resources.set(newLocator.resources.get());
        IpResourceIntervalStrategy<AsnRange> strategy =
            IpResourceIntervalStrategy.<AsnRange>getInstance();
        newLocator.resources.set(
            new NestedIntervalMap<AsnRange, RDAPAuthority>(strategy));
    }

    /**
     * {@inheritDocs}
     */
    @Override
    public void putResourceMapping(AsnRange resource, RDAPAuthority authority)
    {
        RDAPAuthority estAuthority =
            resources.get().findExactOrFirstLessSpecific(resource);

        if(estAuthority == null || estAuthority.equals(authority) == false)
        {
            resources.get().put(resource, authority);
        }
    }
}
