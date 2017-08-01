package net.apnic.rdap.nro.config;

import net.apnic.rdap.nro.scraper.NROScraper;

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
    public NROScraper nroScraper()
    {
        if(baseURI != null || baseURI.isEmpty() == false)
        {
            return new NROScraper(baseURI);
        }
        return new NROScraper();
    }

    public void setBaseURI(String baseURI)
    {
        this.baseURI = baseURI;
    }
}
