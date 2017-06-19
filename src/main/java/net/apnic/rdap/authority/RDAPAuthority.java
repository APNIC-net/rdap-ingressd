package net.apnic.rdap.authority;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents an entity and their authorative data RDAP server.
 *
 * Such authorities would be RIR's and TLD operators.
 */
public class RDAPAuthority
{
    private static final String BEST_SCHEME = "https";

    private List<String> aliases = new ArrayList<String>();
    private String name;
    private List<URI> servers = new ArrayList<URI>();
    private URI defaultServerURI = null;

    /**
     * Constructs a new authority with the given name.
     *
     * The name provided is trimmed and converted to lower case.
     *
     * @param name Unique name to represent this authrotiy
     * @throws IllegalArgumentException When name is null or trim().emtpy()
     */
    public RDAPAuthority(String name)
        throws IllegalArgumentException
    {
        if(name == null || name.trim().isEmpty())
        {
            throw new IllegalArgumentException("name cannot be null or emtpy");
        }

        this.name = name.trim().toLowerCase();
    }

    /**
     * Add an aliases by which this authority can also be known as.
     *
     * The alias provided is trimmed and converted to lower case.
     *
     * @param alias Unique alias that this authority can also be known as
     * @throws IllegalArgumentException When alias is null or trim().empty()
     */
    public void addAlias(String alias)
        throws IllegalArgumentException
    {
        if(alias == null || alias.trim().isEmpty())
        {
            throw new IllegalArgumentException("alias cannot be null or emtpy");
        }
        aliases.add(alias.trim().toLowerCase());
    }

    /**
     * Adds a list of aliases by which this authority can also be known as.
     *
     * The provided aliases are trimmed and converted to lower case.
     *
     * @param aliases List of unique aliases that this authority can otherwise
     *                be known as.
     * @throws IllegalArgumentException When aliases is null or a list item is
     *                                  null or trim().empty()
     */
    public void addAliases(List<String> aliases)
        throws IllegalArgumentException
    {
        if(aliases == null)
        {
            throw new IllegalArgumentException("aliases cannot by null");
        }

        for(String alias : aliases)
        {
            addAlias(alias);
        }
    }

    /**
     * Adds an RDAP server URI to this authority.
     *
     * @param server Authorative RDAP url for this authority.
     * @throws IllegalArgumentException When server URI is null
     */
    public void addServer(URI server)
    {
        if(server == null)
        {
            throw new IllegalArgumentException("server uri cannot be null");
        }
        servers.add(normalizeServerURI(server));
    }

    /**
     * Adds a list server URI's for this authority.
     *
     * @param servers List of authorative RDAP url's for this authority.
     * @throws IllegalArgumentException When servers is null or servers
     *                                  contains null element.
     */
    public void addServers(List<URI> servers)
    {
        if(servers == null || servers.contains(null))
        {
            throw new IllegalArgumentException("servers cannot be null");
        }

        for(URI server : servers)
        {
            addServer(server);
        }
    }

    public static RDAPAuthority createAnonymousAuthority()
    {
        return new RDAPAuthority(UUID.randomUUID().toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object compare)
    {
        if(this == compare)
        {
            return true;
        }
        else if(compare == null || getClass() != compare.getClass())
        {
            return false;
        }

        RDAPAuthority authority = (RDAPAuthority)compare;

        return getName().equals(authority.getName()) &&
               aliases.equals(authority.getAliases()) &&
               servers.equals(authority.getServers());
    }

    /**
     * Returns a list of aliases registered for this authority.
     *
     * @return List of registered authorities
     */
    public List<String> getAliases()
    {
        return aliases;
    }

    /**
     * Returns whats is considered the best URI to use for this RDAPAuthority.
     *
     * @return Best URI to communicate with this server on.
     */
    public URI getDefaultServerURI()
    {
        if(defaultServerURI == null)
        {
            for(URI serverURI : servers)
            {
                defaultServerURI = serverURI;
                if(serverURI.getScheme().equals(BEST_SCHEME))
                {
                    break;
                }
            }
        }
        return defaultServerURI;
    }

    /**
     * Returns the name for this authority.
     *
     * @return name for this authority
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Returns a list of servers registered for this authority.
     *
     * @return List of registered servers
     */
    public List<URI> getServers()
    {
        return servers;
    }

    public static URI normalizeServerURI(URI server)
    {
        if(server.toASCIIString().endsWith("/") == false)
        {
            server = URI.create(server.toASCIIString() + "/");
        }
        return server;
    }
}
