package net.apnic.rdap.iana.scraper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.apnic.rdap.authority.RDAPAuthority;
import net.apnic.rdap.authority.RDAPAuthorityStore;
import net.apnic.rdap.autnum.AsnRange;
import net.apnic.rdap.domain.Domain;
import net.apnic.rdap.resource.ResourceStore;
import net.apnic.rdap.scraper.Scraper;
import net.apnic.rdap.util.ConcurrentUtil;

import net.ripe.ipresource.IpRange;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;

/**
 *
 */
public class IANABootstrapScraper
    implements Scraper
{
    private interface ResourceMapper
    {
        public void process(RDAPAuthority authority, BootstrapService service);
    }

    public static final URL ASN_URL;
    public static final URL BASE_URL;
    public static final URL DOMAIN_URL;
    public static final URL IPV4_URL;
    public static final URL IPV6_URL;
    public static final List<String> SUPPORTED_VERSIONS = Arrays.asList("1.0");

    private static final Logger LOGGER =
        Logger.getLogger(IANABootstrapScraper.class.getName());

    private RDAPAuthorityStore authorityStore = null;
    private ResourceStore<AsnRange> asnStore = null;
    private ResourceStore<Domain> domainStore = null;
    private ResourceStore<IpRange> ipStore = null;
    private HttpHeaders requestHeaders = null;
    private AsyncRestTemplate restClient = null;

    /*
     * Static init of URL members. We cannot do them in the normal way as
     * contruction throws a checked error that needs to be caught.
     */
    static
    {
        URL asnURL = null;
        URL baseURL = null;
        URL domainURL = null;
        URL ipv4URL = null;
        URL ipv6URL = null;

        try
        {
            baseURL = new URL("https://data.iana.org/rdap/");
            asnURL = new URL(baseURL, "asn.json");
            domainURL = new URL(baseURL, "dns.json");
            ipv4URL = new URL(baseURL, "ipv4.json");
            ipv6URL = new URL(baseURL, "ipv6.json");
        }
        catch(MalformedURLException ex)
        {
            LOGGER.log(Level.SEVERE, "Exception when generating IANA url's",
                       ex);

            throw new RuntimeException(ex);
        }
        finally
        {
            ASN_URL = asnURL;
            BASE_URL = baseURL;
            DOMAIN_URL = domainURL;
            IPV4_URL = ipv4URL;
            IPV6_URL = ipv6URL;
        }
    }

    public IANABootstrapScraper(RDAPAuthorityStore authorityStore,
                                ResourceStore<AsnRange> asnStore,
                                ResourceStore<IpRange> ipStore)
    {
        this.authorityStore = authorityStore;
        this.asnStore = asnStore;
        this.ipStore = ipStore;
        restClient = new AsyncRestTemplate();
        setupRequestHeaders();
    }

    private void setupRequestHeaders()
    {
        requestHeaders = new HttpHeaders();
        requestHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        requestHeaders.add(HttpHeaders.USER_AGENT, "");
    }

    @Override
    public void start()
    {
        List<CompletableFuture<Void>> updateFutures = new ArrayList<>();

        updateFutures.add(updateASNData());
    }

    private CompletableFuture<ResponseEntity<JsonNode>>
        makeBootstrapRequest(URL bootStrapURL)
    {
        HttpEntity<?> entity = new HttpEntity<>(requestHeaders);

        try
        {
            ListenableFuture<ResponseEntity<JsonNode>> lFuture =
                restClient.exchange(bootStrapURL.toURI(), HttpMethod.GET,
                                    entity, JsonNode.class);
            return ConcurrentUtil.buildCompletableFuture(lFuture);
        }
        catch(URISyntaxException ex)
        {
            CompletableFuture<ResponseEntity<JsonNode>> cf =
                new CompletableFuture<>();
            cf.completeExceptionally(ex);
            return cf;
        }
    }

    private void parseBootstrapResults(JsonNode bootstrapData,
                                       ResourceMapper mapper)
    {
        JsonNode version = bootstrapData.get("version");
        if(version == null || SUPPORTED_VERSIONS.contains(version.asText()) == false)
        {
            throw new BootstrapVersionException(
                version == null ? "null" : version.asText(), SUPPORTED_VERSIONS);
        }

        ObjectMapper oMapper = new ObjectMapper();
        BootstrapResult result = null;

        try
        {
            result = oMapper.treeToValue(bootstrapData, BootstrapResult.class);
        }
        catch(JsonProcessingException ex)
        {
            throw new RuntimeException(ex);
        }

        for(BootstrapService service : result.getServices())
        {
            List<URL> serviceURLs = null;

            try
            {
                serviceURLs = service.getServersByURL();
            }
            catch(MalformedURLException ex)
            {
                throw new RuntimeException(ex);
            }

            RDAPAuthority authority =
                authorityStore.findAuthorityByURL(serviceURLs);

            if(authority == null)
            {
                authority = RDAPAuthority.createAnonymousAuthority();
                authority.addServers(serviceURLs);
                authorityStore.addAuthority(authority);
            }

            try
            {
                mapper.process(authority, service);
            }
            catch(Exception ex)
            {
                throw new RuntimeException(ex);
            }
        }
    }

    private CompletableFuture<Void> updateASNData()
    {
        return makeBootstrapRequest(ASN_URL)
            .thenAccept((ResponseEntity<JsonNode> entity) ->
            {
                parseBootstrapResults(entity.getBody(),
                    (RDAPAuthority authority, BootstrapService service) ->
                    {
                        for(String strAsnRange : service.getResources())
                        {
                            AsnRange asnRange = AsnRange.parse(strAsnRange);
                            asnStore.putResourceMapping(asnRange, authority);
                        }
                    });
            });
    }

    private CompletableFuture<Void> updateDomainData()
    {
        return makeBootstrapRequest(DOMAIN_URL)
            .thenAccept((ResponseEntity<JsonNode> entity) ->
            {
                parseBootstrapResults(entity.getBody(),
                    (RDAPAuthority authority, BootstrapService service) ->
                    {
                    });
            });
    }

    private CompletableFuture<Void> updateIPAllData(URL ipBootstrapURL)
    {
        return makeBootstrapRequest(ipBootstrapURL)
            .thenAccept((ResponseEntity<JsonNode> entity) ->
            {
                parseBootstrapResults(entity.getBody(),
                    (RDAPAuthority authority, BootstrapService service) ->
                    {
                        for(String strIpRange : service.getResources())
                        {
                            IpRange ipRange = IpRange.parse(strIpRange);
                            ipStore.putResourceMapping(ipRange, authority);
                        }
                    });
            });
    }

    private CompletableFuture<Void> updateIPv4Data()
    {
        return updateIPAllData(IPV4_URL);
    }

    private CompletableFuture<Void> updateIPv6Data()
    {
        return updateIPAllData(IPV6_URL);
    }
}
