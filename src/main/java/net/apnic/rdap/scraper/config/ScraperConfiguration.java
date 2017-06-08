package net.apnic.rdap.scraper.config;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;

import net.apnic.rdap.scraper.Scraper;
import net.apnic.rdap.scraper.ScraperScheduler;

import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@Configuration
@ConfigurationProperties(prefix="rdap.scraping.config")
public class ScraperConfiguration
    implements ApplicationContextAware
{
    private static final Logger LOGGER =
        Logger.getLogger(ScraperConfiguration.class.getName());

    private ApplicationContext applicationContext = null;
    private boolean initialised = false;
    private List<String> order = null;
    private ScraperScheduler scraperScheduler = new ScraperScheduler();

    public List<String> getOrder()
    {
        return order;
    }

    @PostConstruct
    public void init()
    {
        if(initialised)
        {
            return;
        }

        initScraper();

        LOGGER.log(Level.INFO, "Starting scraper scheduler");
        scraperScheduler.start();
        initialised = true;
    }

    private void initScraper()
    {
        LOGGER.log(Level.INFO, "Initialising scraper scheduler with " + order);

        for(String scraperName : order)
        {
            try
            {
                Scraper scraper = applicationContext.getBean(scraperName,
                                                             Scraper.class);
                scraperScheduler.addScraper(scraper);
                LOGGER.log(Level.INFO, "Adding scraper " + scraperName +
                           " to scheduler");
            }
            catch(BeansException ex)
            {
                LOGGER.log(Level.WARNING, "Failed to add scraper " +
                           scraperName, ex);
            }
        }
    }

    @Bean
    public ScraperScheduler scraperScheduler()
    {
        return scraperScheduler;
    }

    public void setApplicationContext(ApplicationContext context)
    {
        this.applicationContext = context;
    }

    public void setOrder(List<String> order)
    {
        this.order = order;
    }
}
