package net.apnic.rdap.iana.scraper;

import net.apnic.rdap.authority.RDAPAuthority;
import net.apnic.rdap.authority.RDAPAuthorityStore;
import net.apnic.rdap.autnum.AsnRange;
import net.apnic.rdap.resource.ResourceMapping;
import net.apnic.rdap.scraper.ScraperException;
import net.apnic.rdap.scraper.ScraperResult;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IANABootstrapScraperTest
{
    private RDAPAuthorityStore authorityStore = new RDAPAuthorityStore();

    private static BootstrapResult makeTestBootstrapResult(List<String> uris, List<String> resources) {
        BootstrapResult bsr = new BootstrapResult();
        bsr.setServices(Arrays.asList(new BootstrapService(Arrays.asList(resources, uris))));
        return bsr;
    }

    /* Method tests joining of autnums during scraping of IANA data. We expect
     * that autnum blocks that follow each other are joined together in one
     * larger block. The first entry in scraping that breaks the larger block
     * creation starts a new block. For an explanation of this behaviour please
     * see IANABootstrapScraper::updateAsnData
     */
    @Test
    void testAutnumJoining() throws ScraperException {
        final String AUTHORITY_URI = "https://test.test";
        IANABootstrapFetcher fetcher = mock(IANABootstrapFetcher.class);
        when(fetcher.makeRequestForType(any()))
                .thenReturn(makeTestBootstrapResult(
                        Collections.singletonList(AUTHORITY_URI),
                        Collections.emptyList()));
        when(fetcher.makeRequestForType(IANABootstrapFetcher.RequestType.ASN))
            .thenReturn(makeTestBootstrapResult(
                Collections.singletonList(AUTHORITY_URI),
                Arrays.asList("1-4", "6-8", "9-10", "11-20", "22-25")));

        IANABootstrapScraper scraper = new IANABootstrapScraper(authorityStore, fetcher);

        ScraperResult scraperResult = scraper.fetchData();
        Optional<List<ResourceMapping<AsnRange>>> asnMappings = scraperResult.getAsnMappings();
        RDAPAuthority rdapAuthority = authorityStore.findAuthorityByURI(URI.create(AUTHORITY_URI));

        Assert.assertTrue(asnMappings.isPresent());
        Assert.assertThat(asnMappings.get().size(), Matchers.is(3));
        Assert.assertThat(asnMappings.get(),
                Matchers.hasItem(new ResourceMapping<>(AsnRange.parse("AS1-AS4"), rdapAuthority)));
        Assert.assertThat(asnMappings.get(),
                Matchers.hasItem(new ResourceMapping<>(AsnRange.parse("AS6-AS20"), rdapAuthority)));
        Assert.assertThat(asnMappings.get(),
                Matchers.hasItem(new ResourceMapping<>(AsnRange.parse("AS22-AS25"), rdapAuthority)));
    }
}
