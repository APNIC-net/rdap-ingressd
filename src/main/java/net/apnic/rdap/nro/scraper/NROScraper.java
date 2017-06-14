package net.apnic.rdap.nro.scraper;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.apnic.rdap.autnum.AsnRange;
import net.apnic.rdap.authority.RDAPAuthorityStore;
import net.apnic.rdap.resource.ResourceStore;
import net.apnic.rdap.stats.scraper.DelegatedStatsScraper;

import net.ripe.ipresource.IpRange;

/**
 * Scraper for NRO delegated stats
 */
public class NROScraper
    extends DelegatedStatsScraper
{
    public static final URI NRO_STATS_URI;

    private static final Logger LOGGER =
        Logger.getLogger(NROScraper.class.getName());

    static
    {
        URI nroStatsURI = null;

        try
        {
            nroStatsURI = new URI("https://www.nro.net/wp-content/uploads/apnic-uploads/delegated-extended");
        }
        catch(URISyntaxException ex)
        {
            LOGGER.log(Level.SEVERE, "Exception when generating NRO uri", ex);
        }
        finally
        {
            NRO_STATS_URI = nroStatsURI;
        }
    }

    public NROScraper(RDAPAuthorityStore authorityStore,
                      ResourceStore<AsnRange> asnStore,
                      ResourceStore<IpRange> ipStore)
    {
        super(NRO_STATS_URI, authorityStore, asnStore, ipStore);
    }

    public String getName()
    {
        return "nro-scraper";
    }
}
