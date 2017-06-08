package net.apnic.rdap.iana.scraper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URISyntaxException;
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

    public static final URI ASN_URI;
    public static final String BASE_URI_STR = "https://data.iana.org./rdap/";
    public static final URI DOMAIN_URI;
    public static final URI IPV4_URI;
    public static final URI IPV6_URI;
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
     * Static init of URI members. We cannot do them in the normal way as
     * contruction throws a checked error that needs to be caught.
     */
    static
    {
        URI asnURI = null;
        URI domainURI = null;
        URI ipv4URI = null;
        URI ipv6URI = null;

        try
        {
            asnURI = new URI(BASE_URI_STR + "asn.json");
            domainURI = new URI(BASE_URI_STR + "dns.json");
            ipv4URI = new URI(BASE_URI_STR + "ipv4.json");
            ipv6URI = new URI(BASE_URI_STR + "ipv6.json");
        }
        catch(URISyntaxException ex)
        {
            LOGGER.log(Level.SEVERE, "Exception when generating IANA url's",
                       ex);
            throw new RuntimeException(ex);
        }
        finally
        {
            ASN_URI = asnURI;
            DOMAIN_URI = domainURI;
            IPV4_URI = ipv4URI;
            IPV6_URI = ipv6URI;
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
        updateFutures.add(updateIPv4Data());
        updateFutures.add(updateIPv6Data());
    }

    private CompletableFuture<ResponseEntity<JsonNode>>
        makeBootstrapRequest(URI bootStrapURI)
    {
        HttpEntity<?> entity = new HttpEntity<>(requestHeaders);

        ListenableFuture<ResponseEntity<JsonNode>> lFuture =
            restClient.exchange(bootStrapURI, HttpMethod.GET,
                                entity, JsonNode.class);
        return ConcurrentUtil.buildCompletableFuture(lFuture);
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
            List<URI> serviceURIs = null;

            try
            {
                serviceURIs = service.getServersByURI();
            }
            catch(URISyntaxException ex)
            {
                throw new RuntimeException(ex);
            }

            RDAPAuthority authority =
                authorityStore.findAuthorityByURI(serviceURIs);

            if(authority == null)
            {
                authority = RDAPAuthority.createAnonymousAuthority();
                authority.addServers(serviceURIs);
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
        return makeBootstrapRequest(ASN_URI)
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
        return makeBootstrapRequest(DOMAIN_URI)
            .thenAccept((ResponseEntity<JsonNode> entity) ->
            {
                parseBootstrapResults(entity.getBody(),
                    (RDAPAuthority authority, BootstrapService service) ->
                    {
                    });
            });
    }

    private CompletableFuture<Void> updateIPAllData(URI ipBootstrapURI)
    {
        return makeBootstrapRequest(ipBootstrapURI)
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
        return updateIPAllData(IPV4_URI);
    }

    private CompletableFuture<Void> updateIPv6Data()
    {
        return updateIPAllData(IPV6_URI);
    }
}
