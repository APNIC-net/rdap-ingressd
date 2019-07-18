package net.apnic.rdap.authority;

import lombok.Data;
import net.apnic.rdap.authority.routing.RoutingAction;
import org.apache.commons.lang.Validate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Main configuration class for all authority related config properties and
 * setup.
 */
@Configuration
@ConfigurationProperties(prefix="rdap")
public class AuthorityConfiguration {
    private static final Logger LOGGER = Logger.getLogger(AuthorityConfiguration.class.getName());

    private List<AuthorityConfig> authorities;
    private RoutingConfig routing;

    /**
     * Class represents a single authority definition under rdap.authorities
     * in the application-rdap.yml config file.
     */
    @Data
    public static class AuthorityConfig {
        private List<String> aliases;
        private String name;
        private AuthorityRoutingConfig routing;
        private List<String> ianaBootstrapRefServers;
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
            RDAPAuthority.setDefaultRoutingAction(this.defaultAction);
        }

        public void setDefaultAuthority(String defaultAuthority)
        {
            this.defaultAuthority = defaultAuthority;
        }
    }

    @Data
    public static class AuthorityRoutingConfig {
        private RoutingAction action;
        private String target;
        private String notFoundFallbackAuthority;
    }

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
        for(AuthorityConfig aConfig : authorities)
        {
            Validate.notNull(aConfig.getRouting());

            RDAPAuthority authority = aConfig.getRouting().getAction() == null
                    ? new RDAPAuthority(aConfig.getName())
                    : new RDAPAuthority(aConfig.getName(), aConfig.getRouting().getAction());

            authority.setRoutingTarget(URI.create(aConfig.getRouting().getTarget()));

            if(aConfig.getAliases() != null)
            {
                authority.setAliases(aConfig.getAliases());
            }

            if(aConfig.getIanaBootstrapRefServers() != null) {
                authority.setIanaBootstrapRefServers(
                        aConfig.getIanaBootstrapRefServers().stream()
                            .map(URI::create)
                            .collect(Collectors.toList()));
            }

            authorityStore().addAuthority(authority);
        }

        // sets fallback authorities
        authorities.stream()
                .filter(c -> c.getRouting().getNotFoundFallbackAuthority() != null)
                .forEach(c -> {
                    RDAPAuthority fallback =
                            authorityStore().findAuthority(c.getRouting().getNotFoundFallbackAuthority());
                    if (fallback == null) {
                        LOGGER.severe(String.format(
                                "Not found fallback authority \"%s\" configured for authority \"%s\" couldn't be found.",
                                c.getRouting().getNotFoundFallbackAuthority(), c.getName()));
                    }

                    authorityStore().findAuthority(c.getName()).setNotFoundFallback(fallback);
                });
    }
}
