package net.apnic.rdap.scraper;

import io.prometheus.client.Gauge;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.apnic.rdap.resource.store.ResourceStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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
    private static final Gauge PROMETHEUS_SCRAPER_STATUS =
            Gauge.build()
                    .name("rdap_ingressd_scraper_status")
                    .help("rdap-ingressd scraper status (" + getScraperStatusHelpString() + ")")
                    .labelNames("scraper")
                    .register();
    private static final Gauge PROMETHEUS_SCRAPER_LAST_SUCCESSFUL_UPDATE_DATETIME =
            Gauge.build()
                    .name("rdap_ingressd_scraper_last_successful_update_datetime")
                    .help("rdap-ingressd scraper last successful update")
                    .labelNames("scraper")
                    .register();

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
        ScraperStatus scraperStatus = new ScraperStatus(scraper.getName());
        scraperStatus.setStatus(Status.INITIALISING);
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

        executor.scheduleAtFixedRate(processDataUpdate(), 0, SCHEDULER_PERIOD, SCHEDULER_PERIOD_UNIT);
    }

    Runnable processDataUpdate() {
        return () -> {
            ResourceStore newResourceStore = resourceStore.initialiseNew();

            for (Scraper scraper : scrapers) {
                try {
                    LOGGER.log(Level.INFO, "Running scraper " + scraper.getName());

                    ScraperResult result = scraper.fetchData();
                    newResourceStore.addScraperResult(result);

                    ScraperStatus scraperStatus = scraperStatuses.get(scraper.getName());
                    scraperStatus.setStatus(Status.UP_TO_DATE);
                    scraperStatus.setLastSuccessfulResult(result);
                    scraperStatus.setLastSuccessfulDateTime(LocalDateTime.now());

                    LOGGER.log(Level.INFO, "Finished scraper " + scraper.getName());
                } catch(ScraperException ex) {
                    LOGGER.log(Level.SEVERE, "Exception when running scraper " + scraper.getName(), ex);

                    ScraperStatus scraperStatus = scraperStatuses.get(scraper.getName());
                    scraperStatus.setStatus(scraperStatus.status.equals(Status.INITIALISING)
                            ? Status.INITIALISATION_FAILED
                            : Status.OUT_OF_DATE);

                    // uses the last successfully fetched data if available
                    if (scraperStatus.getLastSuccessfulResult() != null) {
                        newResourceStore.addScraperResult(scraperStatus.getLastSuccessfulResult());
                    }
                }
            }

            resourceStore.moveStore(newResourceStore);
        };
    }

    public Map<String, ScraperStatus> getScraperStatuses() {
        return scraperStatuses;
    }

    @Override
    public Health health() {
        Health.Builder builder = Health.up();

        for (Map.Entry<String, ScraperStatus> entry : scraperStatuses.entrySet()) {
            LocalDateTime lastSuccessfulDateTime = entry.getValue().lastSuccessfulDateTime;
            builder.withDetail(entry.getKey() ,
                    new StatusHealthEntry(entry.getValue().status.toString(),
                            lastSuccessfulDateTime == null
                                    ? "never"
                                    : DateTimeFormatter.ISO_DATE_TIME.format(lastSuccessfulDateTime)));
        }

        return builder.build();
    }

    private static String getScraperStatusHelpString() {
        Status[] statuses = Status.values();
        String[] strings = new String[statuses.length];

        for (int counter = 0; counter < statuses.length; counter++) {
            strings[counter] = counter + ": " + statuses[counter];
        }

        return String.join(", ", strings);
    }

    @Getter
    static class ScraperStatus {
        private final String scraperName;
        @Setter private ScraperResult lastSuccessfulResult;
        private LocalDateTime lastSuccessfulDateTime;
        private Status status;

        private ScraperStatus(String scraperName) {
            this.scraperName = scraperName;
        }

        public void setLastSuccessfulDateTime(LocalDateTime lastSuccessfulDateTime) {
            this.lastSuccessfulDateTime = lastSuccessfulDateTime;
            PROMETHEUS_SCRAPER_LAST_SUCCESSFUL_UPDATE_DATETIME.labels(scraperName).set(
                    lastSuccessfulDateTime.toEpochSecond(ZoneOffset.UTC));
        }

        public void setStatus(Status status) {
            this.status = status;
            PROMETHEUS_SCRAPER_STATUS.labels(scraperName).set(status.ordinal());
        }
    }

    @AllArgsConstructor
    @Getter
    static class StatusHealthEntry {
        private String status;
        private String lastSuccessfulDateTime;
    }

    enum Status {UP_TO_DATE, OUT_OF_DATE, INITIALISING, INITIALISATION_FAILED}
}
