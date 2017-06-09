package net.apnic.rdap.resource;

import net.apnic.rdap.authority.RDAPAuthority;

/**
 * Interface for resource location classes to implement.
 *
 * This interface is implemented by classes that want to offer resource
 * location functionality.
 *
 * @param <Resource> The type of resource this interface assists in looking for.
 */
public interface ResourceLocator<Resource>
{
    /**
     * Looks for an RDAPAuthority that can answer queries for the supplied
     * resource.
     *
     * @param resource The resource to look for
     * @return The RDAPAuthority for the supplied resource.
     * @throws ResourceNotFoundException When no authority for the resource
     *                                   can be located.
     */
    public RDAPAuthority authorityForResource(Resource resource)
        throws ResourceNotFoundException;
}
