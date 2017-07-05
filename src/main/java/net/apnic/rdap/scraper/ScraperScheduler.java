package net.apnic.rdap.scraper;

import net.apnic.rdap.authority.RDAPAuthorityStore;
import net.apnic.rdap.resource.store.ResourceStore;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Scheduler is responsible for running one or more scraping tasks one after the
 * other building a new ResourceStore for each iteration.
 *
 * Class is to only be used by the creating thread.
 */
@Service
public class ScraperScheduler
{
    private static final int SCHEDULER_PERIOD = 12;
    private static final TimeUnit SCHEDULER_PERIOD_UNIT = TimeUnit.HOURS;

    private final Logger LOGGER =
        Logger.getLogger(ScraperScheduler.class.getName());

    private RDAPAuthorityStore authorityStore = null;
    private ScheduledExecutorService executor = null;
    private List<Scraper> scrapers = new ArrayList<Scraper>();
    private ResourceStore resourceStore = null;
    private boolean started = false;

    /**
     * Takes the ResourceStore this scheduler is to work on as well as an
     * authority store for mapping resources to rdap authorities.
     *
     * The ResourceStore supplied to this scraper is reconstructed every time
     * this scheduler runs. This is to make sure a consitent view of the data is
     * always available and never in a non-authoritative state.
     *
     * @params resourceStore The ResourceStore to rebuild with this scheduler.
     * @params authorityStore The RDAPAuthorityStore to use for associating
     *                        resources to authorities.
     */
    @Autowired
    public ScraperScheduler(ResourceStore resourceStore,
                            RDAPAuthorityStore authorityStore)
    {
        this.resourceStore = resourceStore;
        this.authorityStore = authorityStore;
        executor = Executors.newScheduledThreadPool(1);
    }

    /**
     * Addds the provided scraper to the execution list for this scheduler.
     *
     * Scrapers will be run by this scheduler in the order added.
     *
     * @param scraper The scraper to add to the scheduler
     */
    public void addScraper(Scraper scraper)
    {
        scrapers.add(scraper);
    }

    /**
     * Indicates if this scheduler has been started.
     *
     * @return Scheduler has been started
     */
    public boolean hasStarted()
    {
        return started;
    }

    /**
     * Starts the scheduler and associated scrapers indefinitely.
     *
     * If this scheduler has already been started previously calls to this
     * method will return immediately.
     */
    public void start()
    {
        if(hasStarted())
        {
            return;
        }
        started = true;

        executor.scheduleAtFixedRate(() ->
        {
            ResourceStore newResourceStore = resourceStore.initialiseNew();

            for(Scraper scraper : scrapers)
            {
                try
                {
                    LOGGER.log(Level.INFO, "Running scraper " +
                               scraper.getName());

                    scraper.start(newResourceStore, authorityStore).join();

                    LOGGER.log(Level.INFO, "Finished scraper " +
                               scraper.getName());
                }
                catch(Exception ex)
                {
                    LOGGER.log(Level.SEVERE, "Exception when running scraper " +
                               scraper.getName(), ex);
                }
            }

            resourceStore.moveStore(newResourceStore);

        }, 0, SCHEDULER_PERIOD, SCHEDULER_PERIOD_UNIT);
    }
}
