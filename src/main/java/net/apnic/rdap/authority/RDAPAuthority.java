package net.apnic.rdap.authority;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import net.apnic.rdap.authority.routing.RoutingAction;

/**
 * Represents an entity and their authorative data RDAP server.
 *
 * Such authorities would be RIR's and TLD operators.
 */
public class RDAPAuthority
{
    private static final String BEST_SCHEME = "https";

    private List<String> aliases = new ArrayList<String>();
    private URI defaultServerURI = null;
    private RDAPAuthorityEventListener eventListener = null;
    private String name;
    private RoutingAction routingAction = null;
    private List<URI> servers = new ArrayList<URI>();

    /**
     *
     */
    public RDAPAuthority(String name)
    {
        this(name, RoutingAction.REDIRECT);
    }

    /**
     * Constructs a new authority with the given name.
     *
     * The name provided is trimmed and converted to lower case.
     *
     * @param name Unique name to represent this authrotiy
     * @throws IllegalArgumentException When name is null or trim().emtpy()
     */
    public RDAPAuthority(String name, RoutingAction routingAction)
        throws IllegalArgumentException
    {
        if(name == null || name.trim().isEmpty())
        {
            throw new IllegalArgumentException("name cannot be null or emtpy");
        }

        this.name = name.trim().toLowerCase();
        this.routingAction = routingAction;
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
        alias = addAliasDetail(alias);
        if(eventListener != null)
        {
            eventListener.authorityAliasesAdded(this, Arrays.asList(alias));
        }
    }

    /**
     * Internal detail function for adding a new single alias mapping to this
     * authority.
     *
     * @param alias to add and normalise
     * @return normalised alias that has been added
     */
    private String addAliasDetail(String alias)
    {
        if(alias == null || alias.trim().isEmpty())
        {
            throw new IllegalArgumentException("alias cannot be null or emtpy");
        }
        alias = alias.trim().toLowerCase();
        aliases.add(alias);
        return alias;
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

        ArrayList<String> normAliases = new ArrayList<String>();

        for(String alias : aliases)
        {
            normAliases.add(addAliasDetail(alias));
        }

        if(eventListener != null)
        {
            eventListener.authorityAliasesAdded(this, normAliases);
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

        if(eventListener != null)
        {
            eventListener.authorityServersAdded(this, Arrays.asList(server));
        }
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
            this.servers.add(normalizeServerURI(server));
        }

        if(eventListener != null)
        {
            eventListener.authorityServersAdded(this, servers);
        }
    }

    /**
     * Creates a new authority with a name that is randomly set.
     *
     * Name is currently generated from a UUID
     *
     * @param routingAction The routing action of the newly created authority
     * @return RDAPAuthority Newly created anonymous authority
     */
    public static RDAPAuthority createAnonymousAuthority(RoutingAction routingAction)
    {
        return new RDAPAuthority(UUID.randomUUID().toString(), routingAction);
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
     * Returns the best URI to use for this RDAPAuthority.
     *
     * @return Best URI for communicating with this server.
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
     * Provides the event listener that has been registered onto this authority.
     *
     * @return RDAPAuthorityEventListener object that has been registered
     */
    public RDAPAuthorityEventListener getEventListener()
    {
        return eventListener;
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
     * Returns the routing action for this authority.
     *
     * @return RoutingAction for this authority
     */
    public RoutingAction getRoutingAction()
    {
        return routingAction;
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

    /**
     * Sets the event listener for this authority.
     *
     * @param eventListener Event listener to set on this authority
     * @see RDAPAuthorityEventListener
     */
    public void setEventListener(RDAPAuthorityEventListener eventListener)
    {
        this.eventListener = eventListener;
    }
}
