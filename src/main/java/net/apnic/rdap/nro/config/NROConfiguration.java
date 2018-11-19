package net.apnic.rdap.nro.config;

import net.apnic.rdap.authority.RDAPAuthorityStore;
import net.apnic.rdap.nro.scraper.NROScraper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration singleton for NRO scraping activities.
 */
@Configuration
@ConfigurationProperties(prefix="rdap.scraping.scrapers.nro")
public class NROConfiguration
{
    String baseURI = null;

    public String getBaseURI()
    {
        return baseURI;
    }

    @ConditionalOnProperty(value="rdap.scraping.scrapers.nro.enabled")
    @Bean(value="nro")
    @Autowired
    public NROScraper nroScraper(RDAPAuthorityStore rdapAuthorityStore)
    {
        if(baseURI != null && baseURI.isEmpty() == false)
        {
            return new NROScraper(rdapAuthorityStore, baseURI);
        }
        return new NROScraper(rdapAuthorityStore);
    }

    public void setBaseURI(String baseURI)
    {
        this.baseURI = baseURI;
    }
}
