package net.apnic.rdap.authority;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties
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

    private RDAPAuthorityStore authorityStore = new RDAPAuthorityStore();

    private List<AuthorityConfig> authorities;

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
            RDAPAuthority authority = new RDAPAuthority(aConfig.getName());
            if(aConfig.getAliases() != null)
            {
                authority.addAliases(aConfig.getAliases());
            }

            if(aConfig.getServers() != null)
            {
                List<URL> servers = aConfig.getServers().stream()
                    .map((String strURL) ->
                    {
                        try
                        {
                            return new URL(strURL);
                        }
                        catch(MalformedURLException ex)
                        {
                            throw new RuntimeException(ex);
                        }
                    })
                    .collect(Collectors.toList());
                authority.addServers(servers);
            }
        }
    }

}
