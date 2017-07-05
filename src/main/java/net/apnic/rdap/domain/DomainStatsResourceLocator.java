package net.apnic.rdap.domain;

import java.util.concurrent.atomic.AtomicReference;
import java.util.HashMap;

import net.apnic.rdap.authority.RDAPAuthority;
import net.apnic.rdap.resource.ResourceLocator;
import net.apnic.rdap.resource.store.ResourceStorage;
import net.apnic.rdap.resource.ResourceNotFoundException;

import net.ripe.ipresource.IpRange;

/**
 * Resource locator for domain name authorities and a store for this
 * information.
 *
 * Class acts as boath a resource locator and a store for Domain resources to
 * authority mappings.
 */
public class DomainStatsResourceLocator
    implements ResourceLocator<Domain>, ResourceStorage<Domain>
{
    private ResourceLocator<IpRange> ipLocator;
    private AtomicReference<HashMap<String, RDAPAuthority>> tldMapRef;

    public DomainStatsResourceLocator(ResourceLocator<IpRange> ipLocator)
    {
        this.ipLocator = ipLocator;
        this.tldMapRef = new AtomicReference<HashMap<String, RDAPAuthority>>(
            new HashMap<String, RDAPAuthority>());
    }

    /**
     * {@inheritDocs}
     */
    @Override
    public RDAPAuthority authorityForResource(Domain domain)
        throws ResourceNotFoundException
    {
        if(domain.isArpa())
        {
            return handleArpaDomain(domain);
        }
        else if(tldMapRef.get().containsKey(domain.getTLD()) == false)
        {
            throw new ResourceNotFoundException();
        }
        return tldMapRef.get().get(domain.getTLD());
    }

    private RDAPAuthority handleArpaDomain(Domain domain)
        throws ResourceNotFoundException
    {
        return ipLocator.authorityForResource(
            DomainUtils.ipAddressForArpaDomain(domain));
    }

    /**
     * {@inheritDocs}
     */
    @Override
    public ResourceStorage<Domain> initialiseNew()
    {
        return new DomainStatsResourceLocator(ipLocator);
    }

    /**
     * {@inheritDocs}
     */
    @Override
    public void moveStorage(ResourceStorage<Domain> domainStorage)
    {
        if(getClass() != domainStorage.getClass())
        {
            return;
        }

        DomainStatsResourceLocator newLocator =
            (DomainStatsResourceLocator)domainStorage;

        this.tldMapRef.set(newLocator.tldMapRef.get());
        newLocator.tldMapRef.set(new HashMap<String, RDAPAuthority>());
    }

    /**
     * {@inheritDocs}
     */
    @Override
    public void putResourceMapping(Domain resource, RDAPAuthority authority)
    {
        tldMapRef.get().put(resource.getTLD(), authority);
    }
}
