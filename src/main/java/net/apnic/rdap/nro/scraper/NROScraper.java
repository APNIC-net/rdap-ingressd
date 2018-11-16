package net.apnic.rdap.nro.scraper;

import net.apnic.rdap.authority.RDAPAuthorityStore;
import net.apnic.rdap.stats.scraper.DelegatedStatsScraper;

import java.net.URI;

/**
 * Scraper for NRO delegated stats.
 */
public class NROScraper extends DelegatedStatsScraper {
    private static final String NRO_STATS_URI =
            "https://www.nro.net/wp-content/uploads/apnic-uploads/delegated-extended";

    public NROScraper(RDAPAuthorityStore rdapAuthorityStore) {
        this(rdapAuthorityStore, NRO_STATS_URI);
    }

    public NROScraper(RDAPAuthorityStore rdapAuthorityStore, String uri) {
        super(rdapAuthorityStore, URI.create(uri));
    }

    @Override
    public String getName() {
        return "nro-scraper";
    }
}
