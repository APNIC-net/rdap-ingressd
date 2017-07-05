package net.apnic.rdap.authority.config;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;

import net.apnic.rdap.authority.RDAPAuthority;
import net.apnic.rdap.authority.RDAPAuthorityStore;
import net.apnic.rdap.authority.routing.RoutingAction;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Main configuration class for all authority related config properties and
 * setup.
 */
@Configuration
@ConfigurationProperties(prefix="rdap")
public class AuthorityConfiguration
{
    private static final Logger LOGGER =
        Logger.getLogger(AuthorityConfiguration.class.getName());

    /**
     * Class represents a single authority definition under rdap.authorities
     * in the application-rdap.yml config file.
     */
    public static class AuthorityConfig
    {
        private List<String> aliases;
        private String name;
        private List<String> servers;

        public List<String> getAliases()
        {
            return aliases;
        }

        public String getName()
        {
            return name;
        }

        public List<String> getServers()
        {
            return servers;
        }

        public void setAliases(List<String> aliases)
        {
            this.aliases = aliases;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public void setServers(List<String> servers)
        {
            this.servers = servers;
        }
    }

    /**
     * Class represents the config properties located under rdap.routing in the
     * application-rdap.yml config file.
     */
    public static class RoutingConfig
    {
        private RoutingAction defaultAction;
        private String defaultAuthority;

        public RoutingAction getDefaultAction()
        {
            return defaultAction;
        }

        public String getDefaultAuthority()
        {
            return defaultAuthority;
        }

        public void setDefaultAction(String defaultAction)
        {
            this.defaultAction =
                RoutingAction.getEnum(defaultAction);
        }

        public void setDefaultAuthority(String defaultAuthority)
        {
            this.defaultAuthority = defaultAuthority;
        }
    }

    private List<AuthorityConfig> authorities;
    //private RDAPAuthorityStore authorityStore = new RDAPAuthorityStore();
    private RoutingConfig routing;

    /**
     * RDAPAuthorityStore bean used through the application
     */
    @Bean
    public RDAPAuthorityStore authorityStore()
    {
        return new RDAPAuthorityStore();
    }

    /**
     * Returns the default authority for routing.
     *
     * @return Default routing authority
     */
    @Bean
    public RDAPAuthority defaultAuthority()
    {
        return authorityStore().findAuthority(routing.getDefaultAuthority());
    }

    /**
     * Init method to process configuration after this class has been
     * constructed.
     */
    @PostConstruct
    public void init()
        throws Exception
    {
        try
        {
            setupAuthorityStore();
        }
        catch(Exception ex)
        {
            LOGGER.log(Level.SEVERE,
                       "initialising rdap authority configuration: ", ex);
        }
    }

    /**
     * Sets a list of configuration authorities to process by this class.
     *
     * @param authorities List of configuration authorities
     */
    public void setAuthorities(List<AuthorityConfig> authorities)
    {
        this.authorities = authorities;
    }

    /**
     * Returns a list configuration authorities in use by this class.
     *
     * It is valid behaviour for this function to return null.
     *
     * @return List of configuration authorities
     */
    public List<AuthorityConfig> getAuthorities()
    {
        return authorities;
    }

    /**
     * Sets the routing configuration object used by this class to configure
     * authorities at run time.
     *
     * @param routing RoutingConfig object
     */
    public void setRouting(RoutingConfig routing)
    {
        this.routing = routing;
    }

    /**
     * Returns the routing configuration object in use by this class.
     *
     * It is valid behaviour for this function to return null.
     *
     * @return RoutingConfig in use
     */
    public RoutingConfig getRouting()
    {
        return this.routing;
    }

    /**
     * Sets up the authority store bean used by this application.
     *
     * - Adds any predefined authorities in configuration files to the store
     * - Sets the default routing policy
     * - Configures the default rdap authority
     */
    private void setupAuthorityStore()
    {
        if(routing.getDefaultAction() != null)
        {
            authorityStore().setDefaultRoutingAction(routing.getDefaultAction());
        }

        for(AuthorityConfig aConfig : authorities)
        {
            RDAPAuthority authority = null;

            if(aConfig.getName().equals(routing.getDefaultAuthority()))
            {
                authority = authorityStore().createAuthority(aConfig.getName(),
                                                             RoutingAction.PROXY);
            }
            else
            {
                authority = authorityStore().createAuthority(aConfig.getName());
            }

            if(aConfig.getAliases() != null)
            {
                authority.addAliases(aConfig.getAliases());
            }

            if(aConfig.getServers() != null)
            {
                List<URI> servers = aConfig.getServers().stream()
                    .map((String strURI) ->
                    {
                        try
                        {
                            return new URI(strURI);
                        }
                        catch(URISyntaxException ex)
                        {
                            throw new RuntimeException(ex);
                        }
                    })
                    .collect(Collectors.toList());
                authority.addServers(servers);
            }

            authorityStore().addAuthority(authority);
        }
    }

}
