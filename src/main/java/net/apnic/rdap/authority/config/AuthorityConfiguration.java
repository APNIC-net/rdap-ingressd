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

@Configuration
@ConfigurationProperties(prefix="rdap")
public class AuthorityConfiguration
{
    private static final Logger LOGGER =
        Logger.getLogger(AuthorityConfiguration.class.getName());

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

    public static class RoutingConfig
    {
        private RoutingAction defaultAction;
        private String masterAuthority;

        public RoutingAction getDefaultAction()
        {
            return defaultAction;
        }

        public String getMasterAuthority()
        {
            return masterAuthority;
        }

        public void setDefaultAction(String defaultAction)
        {
            this.defaultAction =
                RoutingAction.getEnum(defaultAction);
        }

        public void setMasterAuthority(String masterAuthority)
        {
            this.masterAuthority = masterAuthority;
        }
    }

    private List<AuthorityConfig> authorities;
    private RDAPAuthorityStore authorityStore = new RDAPAuthorityStore();
    private RoutingConfig routing;

    @Bean
    public RDAPAuthorityStore authorityStore()
    {
        return authorityStore;
    }

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

    public void setAuthorities(List<AuthorityConfig> authorities)
    {
        this.authorities = authorities;
    }

    public List<AuthorityConfig> getAuthorities()
    {
        return authorities;
    }

    public void setRouting(RoutingConfig routing)
    {
        this.routing = routing;
    }

    public RoutingConfig getRouting()
    {
        return this.routing;
    }

    private void setupAuthorityStore()
    {
        if(routing.getDefaultAction() != null)
        {
            authorityStore().setDefaultRoutingAction(routing.getDefaultAction());
        }

        for(AuthorityConfig aConfig : authorities)
        {
            RDAPAuthority authority = null;

            if(aConfig.getName().equals(routing.getMasterAuthority()))
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

            authorityStore.addAuthority(authority);
        }
    }

}
