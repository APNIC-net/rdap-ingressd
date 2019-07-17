package net.apnic.rdap.stats.scraper;

import net.apnic.rdap.authority.RDAPAuthority;
import net.apnic.rdap.authority.RDAPAuthorityStore;
import net.apnic.rdap.autnum.AsnRange;
import net.apnic.rdap.resource.ResourceMapping;
import net.apnic.rdap.scraper.Scraper;
import net.apnic.rdap.scraper.ScraperException;
import net.apnic.rdap.scraper.ScraperResult;
import net.apnic.rdap.stats.parser.DelegatedStatsParser;
import net.apnic.rdap.stats.parser.ResourceRecord;
import net.ripe.ipresource.IpRange;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Abstract scraper for fetching delegated stats files and parsing the results.
 */
public abstract class DelegatedStatsScraper implements Scraper {
    /**
     * Enum contains the supported schemas that can be used as a delegated stats
     * URI.
     */
    private enum SupportedScheme
    {
        HTTP("http"),
        HTTPS("https");

        private final String scheme;

        SupportedScheme(String scheme) {
            this.scheme = scheme;
        }

        @Override
        public String toString()
        {
            return scheme;
        }
    }

    private HttpHeaders requestHeaders = null;
    private RestTemplate restClient = null;
    private SupportedScheme statsScheme = null;
    private URI statsURI = null;
    private RDAPAuthorityStore rdapAuthorityStore;

    /**
     * Construct initialises this DelegatedStatsScraper with an already
     * validated URI.
     *
     * The supplied statsURI must have a scheme that is in SupportedScheme.
     *
     * @param rdapAuthorityStore instance of {@link RDAPAuthorityStore} to retrieve {@link RDAPAuthority} data
     * @param statsURI URI to fetch delegated stats from
     * @throws IllegalArgumentException Thrown when the URI scheme is not
     *                                  supported.
     */
    public DelegatedStatsScraper(RDAPAuthorityStore rdapAuthorityStore, URI statsURI) {
        this.rdapAuthorityStore = rdapAuthorityStore;

        try {
            this.statsScheme = SupportedScheme.valueOf(statsURI.getScheme().toUpperCase());
        } catch(IllegalArgumentException ex) {
            throw new IllegalArgumentException("Non support scheme for URI");
        }

        this.restClient = new RestTemplate();
        this.statsURI = statsURI;
        setupRequestHeaders();
    }

    /**
     * Proxy constructor for DelegatedStatsScraper(URI).
     *
     * Constructs a new URI object from the provided string.
     *
     * @param rdapAuthorityStore instance of {@link RDAPAuthorityStore} to retrieve {@link RDAPAuthority} data
     * @param statsURI URI to fetch delegated stats from
     * @throws URISyntaxException Then a URI object cannot be constructed from
     *                            statsURI
     * @see DelegatedStatsScraper#DelegatedStatsScraper(RDAPAuthorityStore, URI)
     */
    public DelegatedStatsScraper(RDAPAuthorityStore rdapAuthorityStore, String statsURI) throws URISyntaxException {
        this(rdapAuthorityStore, new URI(statsURI));
    }

    /**
     * Proxy function that is capable of taking a delegated stats record and
     * inserting that record into a provided resource store.
     *
     * @param resourceRecord Resource record to handle
     */
    private RDAPAuthority recordAuthority(ResourceRecord resourceRecord) {
        RDAPAuthority authority = rdapAuthorityStore.findAuthority(resourceRecord.getRegistry());

        if(authority == null)
        {
            authority = rdapAuthorityStore.createAuthority(
                resourceRecord.getRegistry());
        }
        return authority;
    }

    /**
     * Function performs the heavy lifting for making a request for a delegated
     * stats file from this classes URI.
     *
     * Function is async and returns a future with a stream of the data returned
     * from the request.
     *
     * @return a stream of the data received from the server
     */
    private InputStream makeDelegatedHttpRequest() {
        HttpEntity<Resource> entity = new HttpEntity<>(requestHeaders);

        try {
            ResponseEntity<Resource> rVal =
                restClient.exchange(statsURI, HttpMethod.GET,
                                    entity, Resource.class);
            return rVal.getBody().getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Initialises and creates a common headers object that is used for all
     * HTTP delegated stats requests.
     */
    private void setupRequestHeaders()
    {
        requestHeaders = new HttpHeaders();
        requestHeaders.setAccept(Arrays.asList(MediaType.TEXT_PLAIN));
        requestHeaders.add(HttpHeaders.USER_AGENT, "");
    }

    @Override
    public ScraperResult fetchData() throws ScraperException {
        if(statsScheme == SupportedScheme.HTTP ||
           statsScheme == SupportedScheme.HTTPS)
        {
            InputStream response = makeDelegatedHttpRequest();
            List<ResourceMapping<IpRange>> ipMappings = new ArrayList<>();
            List<ResourceMapping<AsnRange>> asnMappings = new ArrayList<>();

            try {
                DelegatedStatsParser.parse(response,
                        asnRecord -> asnMappings.add(new ResourceMapping<AsnRange>(asnRecord.toAsnRange(),
                                recordAuthority(asnRecord))),
                        ipRecord -> ipMappings.add(new ResourceMapping<>(ipRecord.toIPRange(),
                                recordAuthority(ipRecord))),
                        ipRecord -> ipMappings.add(new ResourceMapping<>(ipRecord.toIPRange(),
                                recordAuthority(ipRecord))));
            } catch (Exception ex) {
                throw new ScraperException("Error running delegated stats scraper: ", ex);
            }

            return new ScraperResult(ipMappings, asnMappings, null);
        } else {
            throw new ScraperException("Stats scheme not supported: " + statsScheme);
        }
    }
}
