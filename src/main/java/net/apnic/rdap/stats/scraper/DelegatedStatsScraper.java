package net.apnic.rdap.stats.scraper;

import java.io.InputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.Scanner;

import net.apnic.rdap.authority.RDAPAuthority;
import net.apnic.rdap.authority.RDAPAuthorityStore;
import net.apnic.rdap.autnum.AsnRange;
import net.apnic.rdap.resource.ResourceStore;
import net.apnic.rdap.scraper.Scraper;
import net.apnic.rdap.stats.parser.AsnRecord;
import net.apnic.rdap.stats.parser.DelegatedStatsException;
import net.apnic.rdap.stats.parser.DelegatedStatsParser;
import net.apnic.rdap.stats.parser.IPRecord;
import net.apnic.rdap.stats.parser.ResourceRecord;
import net.apnic.rdap.util.ConcurrentUtil;

import net.ripe.ipresource.IpRange;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;

/**
 * Abstract scraper for fetching delegated stats files and parsing the results.
 */
public abstract class DelegatedStatsScraper
    implements Scraper
{
    /**
     * Enum contains the supported schemas that can be used as a delegated stats
     * URI.
     */
    private enum SupportedScheme
    {
        HTTP("http"),
        HTTPS("https");

        private final String scheme;

        private SupportedScheme(String scheme)
        {
            this.scheme = scheme;
        }

        @Override
        public String toString()
        {
            return scheme;
        }
    }

    private ResourceStore<AsnRange> asnStore = null;
    private RDAPAuthorityStore authorityStore = null;
    private ResourceStore<IpRange> ipStore = null;
    private HttpHeaders requestHeaders = null;
    private AsyncRestTemplate restClient = null;
    private SupportedScheme statsScheme = null;
    private URI statsURI = null;

    /**
     * Construct initialises this DelegatedStatsScraper with an already
     * validated URI.
     *
     * The supplied statsURI must have a scheme that is in SupportedScheme.
     *
     * @param statsURI URI to fetch delegated stats from
     * @throws IllegalArgumentException Thrown when the URI scheme is not
     *                                  supported.
     */
    public DelegatedStatsScraper(URI statsURI,
                                 RDAPAuthorityStore authorityStore,
                                 ResourceStore<AsnRange> asnStore,
                                 ResourceStore<IpRange> ipStore)
    {
        try
        {
            SupportedScheme scheme =
                SupportedScheme.valueOf(statsURI.getScheme().toUpperCase());
            this.statsScheme = scheme;
        }
        catch(IllegalArgumentException ex)
        {
            throw new IllegalArgumentException("Non support scheme for URI");
        }

        this.asnStore = asnStore;
        this.authorityStore = authorityStore;
        this.ipStore = ipStore;
        this.restClient = new AsyncRestTemplate();
        this.statsURI = statsURI;
        setupRequestHeaders();
    }

    /**
     * Proxy constructor for DelegatedStatsScraper(URI).
     *
     * Constructs a new URI object from the provided string.
     *
     * @param statsURI URI to fetch delegated stats from
     * @throws URISyntaxException Then a URI object cannot be constructed from
     *                            statsURI
     * @see DelegatedStatsScraper(URI statsURI)
     */
    public DelegatedStatsScraper(String statsURI,
                                 RDAPAuthorityStore authorityStore,
                                 ResourceStore<AsnRange> asnStore,
                                 ResourceStore<IpRange> ipStore)
        throws URISyntaxException
    {
        this(new URI(statsURI), authorityStore, asnStore, ipStore);
    }

    /**
     * Callback to handle a discovered autnum record from a delegated stats
     * file.
     *
     * @param record Asn record from a delegated stats file.
     */
    private void handleAutnumRecord(AsnRecord record)
    {
        handleGenericRecord(record, record.toAsnRange(), asnStore);
    }

    /**
     * Proxy function that is capable of taking a delegated stats record and
     * inserting that record into a provided resource store.
     *
     * @param resourceRecord Resource record to handle
     * @param resource The derived resource from a resourceRecord that gets
     *                 inserted into the provided resourceStore
     * @param resourceStore The Resource store to insert the resource into with
     *                      the authority discovered through the resourceRecord
     */
    private <T> void handleGenericRecord(ResourceRecord resourceRecord,
                                         T resource,
                                         ResourceStore<T> resourceStore)
    {
        RDAPAuthority authority =
            authorityStore.findAuthority(resourceRecord.getRegistry());

        if(authority == null)
        {
            authority = authorityStore.createAuthority(
                resourceRecord.getRegistry());
        }

        resourceStore.putResourceMapping(resource, authority);
    }

    /**
     * Callback to handle a discovered ip record from a delegated stats
     * file.
     *
     * @param record IP record from a delegated stats file.
     */
    private void handleIPRecord(IPRecord record)
    {
        handleGenericRecord(record, record.toIPRange(), ipStore);
    }

    /**
     * Function performs the heavy lifting for making a request for a delegated
     * stats file from this classes URI.
     *
     * Function is async and returns a future with a stream of the data returned
     * from the request.
     *
     * @return Future containing a stream of the data recieved from the server
     */
    private CompletableFuture<InputStream> makeDelegatedHttpRequest()
    {
        HttpEntity<Resource> entity = new HttpEntity<Resource>(requestHeaders);

        ListenableFuture<ResponseEntity<Resource>> lFuture =
            restClient.exchange(statsURI, HttpMethod.GET,
                                entity, Resource.class);

        return ConcurrentUtil.buildCompletableFuture(lFuture)
            .thenApply((ResponseEntity<Resource> responseEntity) ->
            {
                try
                {
                    return responseEntity.getBody().getInputStream();
                }
                catch(IOException ex)
                {
                    throw new RuntimeException(ex);
                }
            });
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

    /**
     * {@inheritDocs}
     */
    @Override
    public CompletableFuture<Void> start()
    {
        CompletableFuture<InputStream> request = null;

        if(statsScheme == SupportedScheme.HTTP ||
           statsScheme == SupportedScheme.HTTPS)
        {
            request = makeDelegatedHttpRequest();
        }

        return request
            .thenAccept((InputStream iStream) ->
            {
                try
                {
                    DelegatedStatsParser.parse(iStream,
                                               this::handleAutnumRecord,
                                               this::handleIPRecord,
                                               this::handleIPRecord);
                }
                catch(Exception ex)
                {
                    throw new RuntimeException(ex);
                }
            });
    }
}
