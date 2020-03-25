package net.apnic.rdap.directory;

import net.apnic.rdap.authority.RDAPAuthority;
import net.apnic.rdap.autnum.AsnRange;
import net.apnic.rdap.domain.Domain;
import net.apnic.rdap.entity.Entity;
import net.apnic.rdap.nameserver.NameServer;
import net.apnic.rdap.resource.ResourceLocator;
import net.apnic.rdap.resource.ResourceNotFoundException;

import net.ripe.ipresource.IpRange;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Public class for performing resource lookup for RDAP path segments.
 *
 * This class supports:
 * - Autnum
 * - Domain
 * - Entity
 * - IP
 * - Nameserver
 */
@Service
public class Directory
{
    private ResourceLocator<AsnRange> asnLocator;
    private ResourceLocator<Domain> domainLocator;
    private ResourceLocator<Entity> entityLocator;
    private ResourceLocator<Void> helpLocator;
    private ResourceLocator<Void> historyLocator;
    private ResourceLocator<IpRange> ipLocator;
    private ResourceLocator<NameServer> nsLocator;
    private ResourceLocator<Void> searchPathLocator;

    @Autowired
    public Directory(ResourceLocator<AsnRange> asnLocator,
                     ResourceLocator<Domain> domainLocator,
                     ResourceLocator<Entity> entityLocator,
                     @Qualifier("helpResourceLocator")
                     ResourceLocator<Void> helpLocator,
                     @Qualifier("historyResourceLocator")
                     ResourceLocator<Void> historyLocator,
                     ResourceLocator<IpRange> ipLocator,
                     ResourceLocator<NameServer> nsLocator,
                     @Qualifier("searchPathLocator")
                     ResourceLocator<Void> searchPathLocator)
    {
        this.asnLocator = asnLocator;
        this.domainLocator = domainLocator;
        this.entityLocator = entityLocator;
        this.helpLocator = helpLocator;
        this.historyLocator = historyLocator;
        this.ipLocator = ipLocator;
        this.nsLocator = nsLocator;
        this.searchPathLocator = searchPathLocator;
    }

    public RDAPAuthority getAutnumAuthority(AsnRange asn)
        throws ResourceNotFoundException
    {
        return locatorProxy(asn, asnLocator);
    }

    public RDAPAuthority getDomainAuthority(Domain domain)
        throws ResourceNotFoundException
    {
        return locatorProxy(domain, domainLocator);
    }

    public RDAPAuthority getEntityAuthority(Entity entity)
        throws ResourceNotFoundException
    {
        return locatorProxy(entity, entityLocator);
    }

    public RDAPAuthority getHelpAuthority()
        throws ResourceNotFoundException
    {
        return locatorProxy(null, helpLocator);
    }

    public RDAPAuthority getHistoryAuthority() throws ResourceNotFoundException {
        return locatorProxy(null, historyLocator);
    }

    public RDAPAuthority getIPAuthority(IpRange block)
        throws ResourceNotFoundException
    {
        return locatorProxy(block, ipLocator);
    }

    public RDAPAuthority getNameServerAuthority(NameServer nameServer)
        throws ResourceNotFoundException
    {
        return locatorProxy(nameServer, nsLocator);
    }

    public RDAPAuthority getSearchPathAuthority()
        throws ResourceNotFoundException
    {
        return locatorProxy(null, searchPathLocator);
    }

    private <Resource> RDAPAuthority locatorProxy(Resource resource,
        ResourceLocator<Resource> locator)
        throws ResourceNotFoundException
    {
        return locator.authorityForResource(resource);
    }
}
