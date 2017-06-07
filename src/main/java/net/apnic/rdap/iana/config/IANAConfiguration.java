package net.apnic.rdap.iana.config;

import net.apnic.rdap.authority.RDAPAuthorityStore;
import net.apnic.rdap.iana.scraper.IANABootstrapScraper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IANAConfiguration
{
    @Bean
    @Autowired
    public IANABootstrapScraper ianaScraper(RDAPAuthorityStore authorityStore)
    {
        return new IANABootstrapScraper(authorityStore);
    }
}
