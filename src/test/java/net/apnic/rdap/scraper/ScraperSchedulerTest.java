package net.apnic.rdap.scraper;

import net.apnic.rdap.authority.RDAPAuthority;
import net.apnic.rdap.autnum.AutnumStatsResourceLocator;
import net.apnic.rdap.domain.DomainStatsResourceLocator;
import net.apnic.rdap.ip.IPStatsResourceLocator;
import net.apnic.rdap.resource.ResourceMapping;
import net.apnic.rdap.resource.ResourceNotFoundException;
import net.apnic.rdap.resource.store.ResourceStore;
import net.apnic.rdap.scraper.ScraperScheduler.StatusHealthEntry;
import net.ripe.ipresource.IpRange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static net.apnic.rdap.scraper.ScraperScheduler.Status.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

class ScraperSchedulerTest {

    private ScraperScheduler scraperScheduler;
    private ResourceStore resourceStore;

    @BeforeEach
    void setup() {
        IPStatsResourceLocator ipStatsResourceLocator = new IPStatsResourceLocator();

        resourceStore = new ResourceStore(
                new AutnumStatsResourceLocator(),
                new DomainStatsResourceLocator(ipStatsResourceLocator),
                ipStatsResourceLocator
        );

        scraperScheduler = new ScraperScheduler(resourceStore);
    }

    @Test
    void testFullScenarioForDataFetching() throws ScraperException, ResourceNotFoundException {
        final RDAPAuthority RDAP_AUTHORITY = new RDAPAuthority("test");
        final RDAPAuthority RDAP_AUTHORITY2 = new RDAPAuthority("test2");
        final IpRange IP_RANGE = IpRange.parse("128.0.0.0/24");
        final String SCRAPER_NAME = "scraper_test";

        Scraper scraper = mock(Scraper.class);
        when(scraper.getName()).thenReturn(SCRAPER_NAME);
        scraperScheduler.addScraper(scraper);

        List<ResourceMapping<IpRange>> ipMappings =
                Collections.singletonList(new ResourceMapping<>(IP_RANGE, RDAP_AUTHORITY));
        ScraperResult result = new ScraperResult(ipMappings, null, null);

        // before fetching data
        assertThrows(ResourceNotFoundException.class,
                () -> ((IPStatsResourceLocator) resourceStore.getIpStorage()).authorityForResource(IP_RANGE));
        assertThat(((StatusHealthEntry) scraperScheduler.health().getDetails().get(SCRAPER_NAME)).getStatus(),
                is(INITIALISING.toString()));

        // fetch data
        when(scraper.fetchData()).thenReturn(result);
        scraperScheduler.processDataUpdate().run();

        // after successfully fetching data
        RDAPAuthority rdapAuthorityReturned =
                ((IPStatsResourceLocator) resourceStore.getIpStorage()).authorityForResource(IP_RANGE);
        assertThat(rdapAuthorityReturned, not(nullValue()));
        assertThat(rdapAuthorityReturned, equalTo(RDAP_AUTHORITY));
        assertThat(((StatusHealthEntry) scraperScheduler.health().getDetails().get(SCRAPER_NAME)).getStatus(),
                is(UP_TO_DATE.toString()));

        // fail data fetching
        when(scraper.fetchData()).thenThrow(ScraperException.class);
        scraperScheduler.processDataUpdate().run();

        // after failed data fetching
        rdapAuthorityReturned =
                ((IPStatsResourceLocator) resourceStore.getIpStorage()).authorityForResource(IP_RANGE);
        assertThat(rdapAuthorityReturned, not(nullValue()));
        assertThat(rdapAuthorityReturned, equalTo(RDAP_AUTHORITY));
        assertThat(((StatusHealthEntry) scraperScheduler.health().getDetails().get(SCRAPER_NAME)).getStatus(),
                is(OUT_OF_DATE.toString()));

        // subsequent successful data fetching
        ipMappings = Collections.singletonList(new ResourceMapping<>(IP_RANGE, RDAP_AUTHORITY2));
        result = new ScraperResult(ipMappings, null, null);
        reset(scraper);
        when(scraper.getName()).thenReturn(SCRAPER_NAME);
        when(scraper.fetchData()).thenReturn(result);
        scraperScheduler.processDataUpdate().run();

        // after successfully fetching data
        rdapAuthorityReturned = ((IPStatsResourceLocator) resourceStore.getIpStorage()).authorityForResource(IP_RANGE);
        assertThat(rdapAuthorityReturned, not(nullValue()));
        assertThat(rdapAuthorityReturned, equalTo(RDAP_AUTHORITY2));
        assertThat(((StatusHealthEntry) scraperScheduler.health().getDetails().get(SCRAPER_NAME)).getStatus(),
                is(UP_TO_DATE.toString()));
    }
}