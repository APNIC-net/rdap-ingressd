package net.apnic.rdap.nro.config;

import net.apnic.rdap.nro.scraper.NROScraper;

import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration singleton for NRO scraping activities.
 */
@Configuration
public class NROConfiguration
{
    @ConditionalOnProperty(value="rdap.scraping.scrapers.iana.enabled")
    @Bean(value="nro")
    public NROScraper nroScraper()
    {
        return new NROScraper();
    }
}
