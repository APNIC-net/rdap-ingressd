package net.apnic.rdap.domain;

import java.util.HashMap;

import net.apnic.rdap.authority.RDAPAuthority;
import net.apnic.rdap.resource.ResourceLocator;
import net.apnic.rdap.resource.ResourceStore;
import net.apnic.rdap.resource.ResourceNotFoundException;

import net.ripe.ipresource.IpRange;

public class DomainStatsResourceLocator
    implements ResourceLocator<Domain>, ResourceStore<Domain>
{
    private ResourceLocator<IpRange> ipLocator;
    private HashMap<String, RDAPAuthority> tldMap =
        new HashMap<String, RDAPAuthority>();

    public DomainStatsResourceLocator(ResourceLocator<IpRange> ipLocator)
    {
        this.ipLocator = ipLocator;
    }

    @Override
    public RDAPAuthority authorityForResource(Domain domain)
        throws ResourceNotFoundException
    {
        if(domain.isArpa())
        {
            return handleArpaDomain(domain);
        }
        throw new ResourceNotFoundException();
    }

    private RDAPAuthority handleArpaDomain(Domain domain)
        throws ResourceNotFoundException
    {
        return ipLocator.authorityForResource(
            DomainUtils.ipAddressForArpaDomain(domain));
    }

    public void putResourceMapping(Domain resource, RDAPAuthority authority)
    {
    }
}
