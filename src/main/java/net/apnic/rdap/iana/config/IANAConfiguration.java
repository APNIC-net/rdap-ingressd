package net.apnic.rdap.iana.config;

import net.apnic.rdap.authority.RDAPAuthorityStore;
import net.apnic.rdap.iana.scraper.IANABootstrapScraper;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration singleton for IANA scraping activities.
 */
@Configuration
public class IANAConfiguration
{
    @ConditionalOnProperty(value="rdap.scraping.scrapers.iana.enabled")
    @Bean(value="iana")
    public IANABootstrapScraper ianaScraper()
    {
        return new IANABootstrapScraper();
    }
}
