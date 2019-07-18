package net.apnic.rdap.authority;

import org.apache.commons.lang.Validate;

import java.net.URI;
import java.util.HashMap;
import java.util.List;

/**
 * Represents a store of authoritive RDAP authorities.
 *
 * Such authorities would be RIR's and TLD operators.
 */
public class RDAPAuthorityStore
{
    private HashMap<String, RDAPAuthority> authoritiesMap = new HashMap<>();
    private HashMap<URI, RDAPAuthority> serverMap = new HashMap<>();

    /**
     * Adds the supplied authority to this store.
     *
     * If an authority with the same name, aliases and server URI already
     * exists it will be overwritten with the new RDAPAuthority.
     *
     * @param authority The RDAPAuthority to add into this store
     * @throws IllegalArgumentException When authority is null
     */
    public void addAuthority(RDAPAuthority authority) {
        Validate.notNull(authority);
        authoritiesMap.put(authority.getName(), authority);
        authority.getAliases().forEach(a -> authoritiesMap.put(a, authority));
        authority.getIanaBootstrapRefServers()
                .forEach(s -> serverMap.put(RDAPAuthority.normalizeServerURI(s), authority));
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

    public RDAPAuthority findAuthorityByURI(URI serverURI)
    {
        return serverMap.get(RDAPAuthority.normalizeServerURI(serverURI));
    }

    public RDAPAuthority findAuthorityByURI(List<URI> serverURIs)
    {
        RDAPAuthority authority = null;
        for(URI serverURI : serverURIs)
        {
            authority = findAuthorityByURI(serverURI);
            if(authority != null)
            {
                return authority;
            }
        }
        return authority;
    }
}
