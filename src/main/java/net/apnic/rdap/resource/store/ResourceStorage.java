package net.apnic.rdap.resource.store;

import net.apnic.rdap.authority.RDAPAuthority;

/**
 * Interface for resource to authority mapping classes to implement.
 *
 * This interface is implemented by classes that want to store mappings between
 * RDAPAuthoritis and their resources.
 *
 * @param <Resource> The type of resource this interface assists in storing.
 */
public interface ResourceStorage<Resource>
{
    public ResourceStorage<Resource> initialiseNew();

    public void moveStorage(ResourceStorage<Resource> newStorage);

    public void putResourceMapping(Resource resource, RDAPAuthority authority)
        throws IllegalArgumentException;
}
