package net.apnic.rdap.authority;

import net.apnic.rdap.authority.routing.RoutingAction;

import java.net.URI;
import java.util.HashMap;
import java.util.List;

/**
 * Represents a store of authoritive RDAP authorities.
 *
 * Such authorities would be RIR's and TLD operators.
 */
public class RDAPAuthorityStore
    implements RDAPAuthorityEventListener
{
    private HashMap<String, RDAPAuthority> authoritiesMap = new HashMap<>();
    private RoutingAction defaultRoutingAction = RoutingAction.REDIRECT;
    private HashMap<URI, RDAPAuthority> serverMap = new HashMap<>();

    /**
     * Adds the supplied authority to this store.
     *
     * If an authority with the same name, aliases and server URI already
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

        authorityAliasesAdded(authority, authority.getAliases());
        authorityServersAdded(authority, authority.getServers());

        authority.setEventListener(this);
    }

    /**
     * {@inheritDocs}
     */
    public void authorityAliasesAdded(RDAPAuthority authority,
                                      List<String> addAliases)
    {
        for(String alias : addAliases)
        {
            authoritiesMap.put(alias, authority);
        }
    }

    /**
     * {@inheritDocs}
     */
    public void authorityServersAdded(RDAPAuthority authority,
                                      List<URI> addServers)
    {
        for(URI serverURI : addServers)
        {
            serverMap.put(RDAPAuthority.normalizeServerURI(serverURI),
                          authority);
        }
    }

    public RDAPAuthority createAnonymousAuthority()
    {
        RDAPAuthority authority = RDAPAuthority.createAnonymousAuthority(
            getDefaultRoutingAction());
        addAuthority(authority);
        return authority;
    }

    /**
     * Creates a new RDAPAuthority for the supplied name and adds it to this
     * store.
     *
     * @param name The new authority name
     * @param RDAPAuthority The newly create authority
     */
    public RDAPAuthority createAuthority(String name)
    {
        return createAuthority(name, getDefaultRoutingAction());
    }

    public RDAPAuthority createAuthority(String name, RoutingAction action)
    {
        RDAPAuthority authority = new RDAPAuthority(name, action);
        addAuthority(authority);
        return authority;
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

    public RoutingAction getDefaultRoutingAction()
    {
        return defaultRoutingAction;
    }

    public void setDefaultRoutingAction(RoutingAction action)
    {
        this.defaultRoutingAction = action;
    }
}
