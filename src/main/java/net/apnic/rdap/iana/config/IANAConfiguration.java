package net.apnic.rdap.iana.config;

import net.apnic.rdap.authority.RDAPAuthorityStore;
import net.apnic.rdap.iana.scraper.IANABootstrapFetcher;
import net.apnic.rdap.iana.scraper.IANABootstrapScraper;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration singleton for IANA scraping activities.
 */
@Configuration
@ConfigurationProperties(prefix="rdap.scraping.scrapers.iana")
public class IANAConfiguration
{
    String baseURI = null;

    public String getBaseURI()
    {
        return baseURI;
    }

    @ConditionalOnProperty(value="rdap.scraping.scrapers.iana.enabled")
    @Bean(value="iana")
    public IANABootstrapScraper ianaScraper()
    {
        if(baseURI != null && baseURI.isEmpty() == false)
        {
            return new IANABootstrapScraper(new IANABootstrapFetcher(baseURI));
        }
        return new IANABootstrapScraper();
    }

    public void setBaseURI(String baseURI)
    {
        this.baseURI = baseURI;
    }
}
