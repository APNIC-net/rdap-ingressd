package net.apnic.rdap.resource;

import net.apnic.rdap.authority.RDAPAuthority;

/**
 * Interface for resource to authority mapping classes to implement.
 *
 * This interface is implemented by classes that want to store mappings between
 * RDAPAuthoritis and their resources.
 *
 * @param <Resource> The type of resource this interface assists in storing.
 */
public interface ResourceStore<Resource>
{
    public void putResourceMapping(Resource resource, RDAPAuthority authority)
        throws IllegalArgumentException;
}
