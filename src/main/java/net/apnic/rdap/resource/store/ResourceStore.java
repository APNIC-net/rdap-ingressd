package net.apnic.rdap.resource.store;

import java.util.HashMap;

import net.apnic.rdap.authority.RDAPAuthority;
import net.apnic.rdap.autnum.AsnRange;
import net.apnic.rdap.domain.Domain;

import net.ripe.ipresource.IpRange;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
     * @see moveStore()
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

    private <Resource> void storageProxy(ResourceStorage<Resource> storage,
                                         Resource resource,
                                         RDAPAuthority authority)
    {
        storage.putResourceMapping(resource, authority);
    }
}
