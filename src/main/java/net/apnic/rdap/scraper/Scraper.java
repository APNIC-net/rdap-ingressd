package net.apnic.rdap.scraper;

import java.util.concurrent.CompletableFuture;

import net.apnic.rdap.authority.RDAPAuthorityStore;
import net.apnic.rdap.resource.store.ResourceStore;

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
     * @param store Resource storage to placed scraped results into
     * @return Future that is fulfilled when the scraper finishes its tasks.
     */
    public CompletableFuture<Void> start(ResourceStore resourceStore,
                                         RDAPAuthorityStore authorityStore);
}
