package net.apnic.rdap.resource;

import net.apnic.rdap.authority.RDAPAuthority;

/**
 * Class provides a static implementation of the ResourceLocator interface.
 *
 * This class will either always return the same RDAPAuthority or always
 * throw a ResourceNotFoundException.
 *
 * @param <Resource> The type of resource this class assists with
 */
public class StaticResourceLocator<Resource>
    implements ResourceLocator<Resource>
{
    private RDAPAuthority staticAuthority = null;

    /**
     * Default constructor
     *
     * Using this constructor will for the class to always throw
     * ResourceNotFoundException's
     */
    public StaticResourceLocator()
    {
    }

    /**
     * Sets the RDAPAuthority to always respond with for resource lookups.
     *
     * @param staticAuthority RDAPAuthority to always respond with
     */
    public StaticResourceLocator(RDAPAuthority staticAuthority)
    {
        this.staticAuthority = staticAuthority;
    }

    /**
     * {@inheritDocs}
     */
    @Override
    public RDAPAuthority authorityForResource(Resource resource)
        throws ResourceNotFoundException
    {
        RDAPAuthority rval = getAuthority();
        if(rval == null)
        {
            throw new ResourceNotFoundException();
        }
        return rval;
    }

    /**
     * Provides the authority that was set on construction.
     *
     * @return RDAPAuthority that was set or null if no authority was set
     */
    public RDAPAuthority getAuthority()
    {
        return staticAuthority;
    }
}
