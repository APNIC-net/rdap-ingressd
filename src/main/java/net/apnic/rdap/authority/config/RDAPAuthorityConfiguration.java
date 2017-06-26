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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="rdap")
public class RDAPAuthorityConfiguration
{
    private static final Logger LOGGER =
        Logger.getLogger(RDAPAuthorityConfiguration.class.getName());

    public static class AuthorityConfig
    {
        private List<String> aliases;
        private String name;
        private List<String> servers;

        public AuthorityConfig()
        {
        }

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

    private List<AuthorityConfig> authorities;
    private RDAPAuthorityStore authorityStore = new RDAPAuthorityStore();

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

    private void setupAuthorityStore()
    {
        for(AuthorityConfig aConfig : authorities)
        {
            RDAPAuthority authority =
                authorityStore().createAuthority(aConfig.getName());
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
