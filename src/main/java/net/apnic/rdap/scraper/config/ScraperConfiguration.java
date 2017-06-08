package net.apnic.rdap.scraper.config;

import net.apnic.rdap.scraper.ScraperScheduler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScraperConfiguration
{
    private ScraperScheduler scraperScheduler = null;

    @Bean
    public ScraperScheduler scraperScheduler()
    {
        if(scraperScheduler != null)
        {
            scraperScheduler = new ScraperScheduler();
        }

        return scraperScheduler;
    }
}
