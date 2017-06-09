package net.apnic.rdap.authority;

import java.net.URL;
import java.util.HashMap;

/**
 * Represents a store of authoritive RDAP authorities.
 *
 * Such authorities would be RIR's and TLD operators.
 */
public class RDAPAuthorityStore
{
    private HashMap<String, RDAPAuthority> authoritiesMap = new HashMap<>();
    private HashMap<URL, RDAPAuthority> serverMap = new HashMap<>();

    /**
     * Default constructor
     */
    public RDAPAuthorityStore()
    {
    }

    /**
     * Adds the supplied authority to this store.
     *
     * If an authority with the same name, aliases and server URL already
     * exists they will be overwritten with the new RDAPAuthority.
     *
     * @param authority The RDAPAuthority to add into this store
     * @throws IllegalArgumentException When authority is null
     */
    public void addAuthority(RDAPAuthority authority)
    {
        if(authority == null)
        {
            throw new IllegalArgumentException("authority cannot be null");
        }
        authoritiesMap.put(authority.getName(), authority);

        for(String alias : authority.getAliases())
        {
            authoritiesMap.put(alias, authority);
        }

        for(URL serverURL : authority.getServers())
        {
            serverMap.put(serverURL, authority);
        }
    }

    /**
     * Finds the authority that is registered for the given name.
     *
     * The supplied name is trimmed and converted to lowercase before the
     * lookup. Method checks both authority aliases and names.
     *
     * @param authorityName The name or alias of the authority to find.
     * @return RDAPAuthority corresponding to the supplied name or null.
     */
    public RDAPAuthority findAuthority(String authorityName)
    {
        return authoritiesMap.get(authorityName.trim().toLowerCase());
    }
}
