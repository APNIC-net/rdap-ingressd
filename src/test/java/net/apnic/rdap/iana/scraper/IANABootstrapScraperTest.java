package net.apnic.rdap.iana.scraper;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.List;

import net.apnic.rdap.authority.RDAPAuthorityStore;
import net.apnic.rdap.resource.store.ResourceStore;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class IANABootstrapScraperTest
{
    private RDAPAuthorityStore authorityStore = new RDAPAuthorityStore();

    private static CompletableFuture<BootstrapResult>
        makeTestBootstrapResult(List<String> uris, List<String> resources)
    {
        BootstrapResult bsr = new BootstrapResult();
        bsr.setServices(Arrays.asList(new BootstrapService(Arrays.asList(resources, uris))));

        return CompletableFuture.completedFuture(bsr);
    }

    @Test
    public void testAutnumJoining()
    {
        IANABootstrapFetcher fetcher = mock(IANABootstrapFetcher.class);
        when(fetcher.makeRequestForType(any(IANABootstrapFetcher.RequestType.class)))
            .thenReturn(makeTestBootstrapResult(
                Arrays.asList("http://test.test"),
                Arrays.asList("1-4", "6-8", "9-10", "11-20", "22-25")));

        ResourceStore store = mock(ResourceStore.class);

        IANABootstrapScraper scraper = new IANABootstrapScraper(fetcher);

        scraper.updateASNData(store, authorityStore);

        verify(store).putAutnumMapping(
            argThat(asnRange -> asnRange.toString().equals("AS1-AS4")), any());
        verify(store).putAutnumMapping(
            argThat(asnRange -> asnRange.toString().equals("AS6-AS20")), any());
        verify(store).putAutnumMapping(
            argThat(asnRange -> asnRange.toString().equals("AS22-AS25")), any());
    }
}
