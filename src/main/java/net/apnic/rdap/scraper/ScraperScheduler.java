package net.apnic.rdap.scraper;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Scheduler is responsible for running scraping tasks.
 */
public class ScraperScheduler
{
    private class Runner
        implements Runnable
    {
        private final Logger LOGGER =
            Logger.getLogger(ScraperScheduler.class.getName());

        private Scraper scraper = null;

        /**
         * Initialises this runner with the given scraper.
         */
        public Runner(Scraper scraper)
        {
            this.scraper = scraper;
        }

        /**
         * {@inheritDocs}
         */
        @Override
        public void run()
        {
            LOGGER.log(Level.INFO, "Running scraper " + scraper.getName());

            try
            {
                scraper.start().join();
                LOGGER.log(Level.INFO, "Finished scraper " + scraper.getName());
            }
            catch(Exception ex)
            {
                LOGGER.log(Level.SEVERE, "Exception when running scraper " +
                           scraper.getName(), ex);
            }
        }
    }

    private ScheduledExecutorService executor = null;
    private List<Scraper> scrapers = new ArrayList<Scraper>();
    private boolean started = false;

    /**
     * Default constructor
     */
    public ScraperScheduler()
    {
        executor = Executors.newScheduledThreadPool(1);
    }

    /**
     * Addds the provided scraper to the list.
     *
     * Scrapers will be run by this scheduler in the order added.
     *
     * @param scraper The scraper to add to the scheduler.
     */
    public void addScraper(Scraper scraper)
    {
        scrapers.add(scraper);
    }

    public void start()
    {
        if(started == true)
        {
            return;
        }

        started = true;

        for(Scraper scraper : scrapers)
        {
            executor.scheduleAtFixedRate(new Runner(scraper), 0, 12,
                                         TimeUnit.HOURS);
        }
    }
}
