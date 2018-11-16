package net.apnic.rdap.scraper;

import net.apnic.rdap.resource.store.ResourceStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Scheduler is responsible for running one or more scraping tasks one after the
 * other building a new ResourceStore for each iteration.
 *
 * Class is to only be used by the creating thread.
 */
@Service
public class ScraperScheduler implements HealthIndicator {
    private static final int SCHEDULER_PERIOD = 12;
    private static final TimeUnit SCHEDULER_PERIOD_UNIT = TimeUnit.HOURS;

    private final Logger LOGGER =
        Logger.getLogger(ScraperScheduler.class.getName());

    private ScheduledExecutorService executor = null;
    private List<Scraper> scrapers = new ArrayList<Scraper>();
    private ResourceStore resourceStore = null;
    private boolean started = false;
    private Map<String, ScraperStatus> scraperStatuses = new HashMap<>();

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
    public ScraperScheduler(ResourceStore resourceStore) {
        this.resourceStore = resourceStore;
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
        ScraperStatus scraperStatus = new ScraperStatus();
        scraperStatus.status = Status.PENDING;
        scraperStatuses.put(scraper.getName(), scraperStatus);
    }

    /**
     * Starts the scheduler and associated scrapers indefinitely.
     *
     * If this scheduler has already been started previously calls to this
     * method will return immediately.
     */
    public void start() {
        synchronized (this) {
            if (started) {
                return;
            }

            started = true;
        }

        executor.scheduleAtFixedRate(() -> {
            ResourceStore newResourceStore = resourceStore.initialiseNew();

            for (Scraper scraper : scrapers) {
                try {
                    LOGGER.log(Level.INFO, "Running scraper " +
                               scraper.getName());

                    ScraperResult result = scraper.fetchData();
                    newResourceStore.addScraperResult(result);

                    ScraperStatus scraperStatus = scraperStatuses.get(scraper.getName());
                    scraperStatus.status = Status.SUCCESS;
                    scraperStatus.lastSuccessfulResult = result;
                    scraperStatus.lastSuccessfulDateTime = LocalDateTime.now();

                    LOGGER.log(Level.INFO, "Finished scraper " +
                               scraper.getName());
                } catch(ScraperException ex) {
                    LOGGER.log(Level.SEVERE, "Exception when running scraper " +
                               scraper.getName(), ex);

                    ScraperStatus scraperStatus = scraperStatuses.get(scraper.getName());
                    scraperStatus.status = Status.FAILURE;

                    // uses the last successfully fetched data if available
                    if (scraperStatus.lastSuccessfulResult != null) {
                        newResourceStore.addScraperResult(scraperStatus.lastSuccessfulResult);
                    }

                }
            }

            resourceStore.moveStore(newResourceStore);

        }, 0, SCHEDULER_PERIOD, SCHEDULER_PERIOD_UNIT);
    }

    public Map<String, ScraperStatus> getScraperStatuses() {
        return scraperStatuses;
    }

    @Override
    public Health health() {
        return Health.up().withDetail("ScraperStatus:", getScraperStatuses().toString()).build();
    }

    private class ScraperStatus {
        ScraperResult lastSuccessfulResult;
        LocalDateTime lastSuccessfulDateTime;
        Status status;

        @Override
        public String toString() {
            return "{" +
                    "lastSuccessfulDateTime=" + lastSuccessfulDateTime +
                    ", status=" + status +
                    '}';
        }
    }

    private enum Status { SUCCESS, FAILURE, PENDING }
}
