package net.apnic.rdap.authority;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import net.apnic.rdap.authority.routing.RoutingAction;
import org.apache.commons.lang.Validate;

/**
 * Represents an entity and their authorative data RDAP server.
 *
 * Such authorities would be RIR's and TLD operators.
 */
@Data
public class RDAPAuthority {

    @Setter(AccessLevel.PACKAGE) private static RoutingAction defaultRoutingAction = RoutingAction.REDIRECT;

    private List<String> aliases = Collections.emptyList();
    private String name;
    private RoutingAction routingAction;
    private URI routingTarget;
    /** Target URL meant to be used for internal direct queries (e.g. inside the same cluster). */
    private Optional<URI> routingInternalTarget;
    private RDAPAuthority notFoundFallback;
    private List<URI> ianaBootstrapRefServers = Collections.emptyList();



    /**
     * Constructs a new authority with the given name and the default routing action.
     *
     * The name provided is trimmed and converted to lower case.
     *
     * @param name Unique name to represent this authrotiy
     * @throws IllegalArgumentException When name is null or trim().emtpy()
     */
    public RDAPAuthority(String name) {
        Validate.notNull(name);
        org.apache.commons.lang3.Validate.notBlank(name);
        this.name = name.trim().toLowerCase();
        this.routingAction = defaultRoutingAction;
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
        Validate.notEmpty(name);

        this.name = name.trim().toLowerCase();
        this.routingAction = routingAction;
    }

    /**
     * Internal detail function for adding a new single alias mapping to this
     * authority.
     *
     * @param alias to add and normalise
     * @return normalised alias that has been added
     */
    private String normaliseAliasDetail(String alias)
    {
        if(alias == null || alias.trim().isEmpty())
        {
            throw new IllegalArgumentException("alias cannot be null or emtpy");
        }
        alias = alias.trim().toLowerCase();
        return alias;
    }

    /**
     * Sets a list of aliases by which this authority can also be known as.
     *
     * The provided aliases are trimmed and converted to lower case.
     *
     * @param aliases List of unique aliases that this authority can otherwise
     *                be known as.
     * @throws IllegalArgumentException When aliases is null or a list item is
     *                                  null or trim().empty()
     */
    public void setAliases(List<String> aliases) throws IllegalArgumentException {
        Validate.notNull(aliases);
        this.aliases = aliases.stream().map(this::normaliseAliasDetail).collect(Collectors.toList());
    }

    /**
     * Creates a new authority with a name that is randomly set.
     *
     * Name is currently generated from a UUID
     *
     * @return RDAPAuthority Newly created anonymous authority
     */
    public static RDAPAuthority createAnonymousAuthority() {
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
               ianaBootstrapRefServers.equals(authority.getIanaBootstrapRefServers());
    }

    public static URI normalizeServerURI(URI server) {
        if(!server.toASCIIString().endsWith("/")) {
            server = URI.create(server.toASCIIString() + "/");
        }
        return server;
    }

    public void setRoutingTarget(URI routingTarget) {
        Validate.notNull(routingTarget);
        this.routingTarget = normalizeServerURI(routingTarget);
    }

    public void setRoutingInternalTarget(Optional<URI> internalTarget) {
        Validate.notNull(internalTarget);
        this.routingInternalTarget = internalTarget.map(RDAPAuthority::normalizeServerURI);
    }

    public void setIanaBootstrapRefServers(List<URI> servers) {
        Validate.notNull(servers);
        this.ianaBootstrapRefServers = servers;
    }
}
