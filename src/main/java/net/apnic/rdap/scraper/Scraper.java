package net.apnic.rdap.scraper;

import java.util.concurrent.CompletableFuture;

/**
 * Generic interface that scrapers must conform to.
 */
public interface Scraper
{
    /**
     * Name of the scraper used for debuging and error logging purposes.
     */
    public String getName();

    /**
     * Main worker method that gets called by the scheduler for each scraper.
     *
     * @return Future that is fulfilled when the scraper finishes it's tasks.
     */
    public CompletableFuture<Void> start();
}
