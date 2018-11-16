package net.apnic.rdap.resource.store;

import net.apnic.rdap.authority.RDAPAuthority;
import net.apnic.rdap.autnum.AsnRange;
import net.apnic.rdap.domain.Domain;
import net.apnic.rdap.resource.ResourceMapping;
import net.apnic.rdap.scraper.ScraperResult;
import net.ripe.ipresource.IpRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * Centralised resource store service for adding authority to resource mappings
 * for routable resources in RDAP
 */
@Service
public class ResourceStore
{
    private ResourceStorage<AsnRange> asnStorage;
    private ResourceStorage<Domain> domainStorage;
    private ResourceStorage<IpRange> ipStorage;

    /**
     * Default constructor
     *
     * Takes the RDAP routable resource storage objects.
     */
    @Autowired
    public ResourceStore(ResourceStorage<AsnRange> asnStorage,
                         ResourceStorage<Domain> domainStorage,
                         ResourceStorage<IpRange> ipStorage)
    {
        this.asnStorage = asnStorage;
        this.domainStorage = domainStorage;
        this.ipStorage = ipStorage;
    }

    /**
     * Creates a new empty ResourceStore modeled off this object.
     *
     * Method is designed to be used in conjunction with moveStore()
     *
     * @return ResourceStore New ResourceStore modeled from this object.
     */
    public ResourceStore initialiseNew()
    {
        return new ResourceStore(asnStorage.initialiseNew(),
                                 domainStorage.initialiseNew(),
                                 ipStorage.initialiseNew());
    }

    /**
     * Places a new AsnRange to authority mapping into the autnum store.
     *
     * @param asn AsnRange object to map with the given RDAPAuthority
     * @param authority RDAPAuthority to map with the given AsnRange
     */
    public void putAutnumMapping(AsnRange asn, RDAPAuthority authority)
    {
        storageProxy(asnStorage, asn, authority);
    }

    /**
     * Places a new Domain to authority mapping into the domain store.
     *
     * @param domain Domain object to map with the given RDAPAuthority
     * @param authority RDAPAuthority to map with the given Domain
     */
    public void putDomainMapping(Domain domain, RDAPAuthority authority)
    {
        storageProxy(domainStorage, domain, authority);
    }

    /**
     * Places a new IpRange to authority mapping into the ip store.
     *
     * @param ip IpRange object to map with the given RDAPAuthority
     * @param authority RDAPAuthority to map with the given IpRange
     */
    public void putIPMapping(IpRange ip, RDAPAuthority authority)
    {
        storageProxy(ipStorage, ip, authority);
    }

    /**
     * Does an inplace move operation from the provided ResourceStore into this
     * object.
     *
     * @param store ResourceStore to move into this object
     */
    public void moveStore(ResourceStore store)
    {
        asnStorage.moveStorage(store.asnStorage);
        domainStorage.moveStorage(store.domainStorage);
        ipStorage.moveStorage(store.ipStorage);
    }

    /**
     * Adds all the results of a {@link net.apnic.rdap.scraper.Scraper} encapsulated in an {@link ScraperResult} to the
     * store.
     * @param result a {@link ScraperResult} encapsulating the data to be added
     */
    public void addScraperResult(ScraperResult result) {
        result.getIpMappings().ifPresent(resourceMappings -> putMapping(resourceMappings, this::putIPMapping));
        result.getAsnMappings().ifPresent(resourceMappings -> putMapping(resourceMappings, this::putAutnumMapping));
        result.getDomainMappings().ifPresent(resourceMappings -> putMapping(resourceMappings, this::putDomainMapping));
    }

    private <Resource> void storageProxy(ResourceStorage<Resource> storage,
                                         Resource resource,
                                         RDAPAuthority authority)
    {
        storage.putResourceMapping(resource, authority);
    }

    private <Resource> void putMapping(List<ResourceMapping<Resource>> resourceMappings,
                                       BiConsumer<Resource, RDAPAuthority> putFunction) {
        for (ResourceMapping<Resource> resourceMapping : resourceMappings) {
            putFunction.accept(resourceMapping.getResource(), resourceMapping.getAuthority());
        }
    }
}
