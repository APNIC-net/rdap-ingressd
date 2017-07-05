package net.apnic.rdap.authority;

import java.net.URI;
import java.util.List;

/**
 * Interface provides callbacks for registering change events on RDAPAuthority
 * classes.
 *
 * Intended use for this class is for RDAPAuthorityStore that needs to maintain
 * data structure lookups for authorities based on name(s) and URIs.
 */
public interface RDAPAuthorityEventListener
{
    /**
     * Callback called when new aliases are added to a given authority.
     *
     * @param authority The authority that has had new aliases added
     * @param addedAliases List of aliases that have been added to the
     *                     authority
     */
    public void authorityAliasesAdded(RDAPAuthority authority,
                                      List<String> addedAliases);

    /**
     * Callback called when new servers are added to a given authority.
     *
     * @param authority The authority that has had new servers added
     * @param addedServers List of servers that have been added to the
     *                     authority.
     */
    public void authorityServersAdded(RDAPAuthority authority,
                                      List<URI> addServers);
}
