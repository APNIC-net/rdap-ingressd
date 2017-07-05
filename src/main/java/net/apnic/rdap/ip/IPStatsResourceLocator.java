package net.apnic.rdap.ip;

import java.util.concurrent.atomic.AtomicReference;

import net.apnic.rdap.authority.RDAPAuthority;
import net.apnic.rdap.resource.ResourceLocator;
import net.apnic.rdap.resource.ResourceNotFoundException;
import net.apnic.rdap.resource.store.ResourceStorage;

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
    implements ResourceLocator<IpRange>, ResourceStorage<IpRange>
{
    private AtomicReference<NestedIntervalMap<IpRange, RDAPAuthority>> resources;

    /**
     * Default constructor
     */
    public IPStatsResourceLocator()
    {
        IpResourceIntervalStrategy<IpRange> strategy =
            IpResourceIntervalStrategy.<IpRange>getInstance();
        resources =
            new AtomicReference<NestedIntervalMap<IpRange, RDAPAuthority>>(
                new NestedIntervalMap<IpRange, RDAPAuthority>(strategy));
    }

    /**
     * {@inheritDocs}
     */
    @Override
    public RDAPAuthority authorityForResource(IpRange range)
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
    public ResourceStorage<IpRange> initialiseNew()
    {
        return new IPStatsResourceLocator();
    }

    /**
     * {@inheritDocs}
     */
    @Override
    public void moveStorage(ResourceStorage<IpRange> ipStorage)
    {
        if(getClass() != ipStorage.getClass())
        {
            return;
        }

        IPStatsResourceLocator newLocator =
            (IPStatsResourceLocator)ipStorage;

        this.resources.set(newLocator.resources.get());
        IpResourceIntervalStrategy<IpRange> strategy =
            IpResourceIntervalStrategy.<IpRange>getInstance();
        newLocator.resources.set(
            new NestedIntervalMap<IpRange, RDAPAuthority>(strategy));
    }

    /**
     * {@inheritDocs}
     */
    @Override
    public void putResourceMapping(IpRange resource, RDAPAuthority authority)
    {
        RDAPAuthority estAuthority =
            resources.get().findExactOrFirstLessSpecific(resource);

        if(estAuthority == null || estAuthority.equals(authority) == false)
        {
            resources.get().put(resource, authority);
        }
    }
}
