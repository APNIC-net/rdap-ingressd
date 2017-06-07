package net.apnic.rdap.iana.scraper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.apnic.rdap.authority.RDAPAuthority;
import net.apnic.rdap.authority.RDAPAuthorityStore;
import net.apnic.rdap.scraper.Scraper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;

public class IANABootstrapScraper
    implements Scraper
{
    public static final List<String> SUPPORTED_VERSIONS = Arrays.asList("1.0");
    public static final URL ASN_URL;
    public static final URL BASE_URL;
    public static final URL DOMAIN_URL;
    public static final URL IPV4_URL;
    public static final URL IPV6_URL;

    private static final Logger LOGGER =
        Logger.getLogger(IANABootstrapScraper.class.getName());

    private RDAPAuthorityStore authorityStore = null;
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

    public IANABootstrapScraper(RDAPAuthorityStore authorityStore)
    {
        this.authorityStore = authorityStore;
        restClient = new AsyncRestTemplate();
        setupRequestHeaders();

        start();
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
        updateASNData();
    }

    private ListenableFuture<ResponseEntity<JsonNode>>
        makeBootstrapRequest(URL bootStrapURL)
    {
        HttpEntity<?> entity = new HttpEntity(requestHeaders);

        try
        {
            return restClient.exchange(bootStrapURL.toURI(), HttpMethod.GET,
                                       entity, JsonNode.class);
        }
        catch(URISyntaxException ex)
        {
            return AsyncResult.<ResponseEntity<JsonNode>>forExecutionException(ex);
        }
    }

    private void parseBootstrapResults(JsonNode bootstrapData)
    {
        JsonNode version = bootstrapData.get("version");
        if(version == null || SUPPORTED_VERSIONS.contains(version.asText()) == false)
        {
            throw new BootstrapVersionException("null", SUPPORTED_VERSIONS);
        }

        ObjectMapper oMapper = new ObjectMapper();
        BootstrapResult result = null;

        try
        {
            result = oMapper.treeToValue(bootstrapData, BootstrapResult.class);
        }
        catch(JsonProcessingException ex)
        {
            System.out.println(ex);
        }

        for(BootstrapService service : result.getServices())
        {
            RDAPAuthority authority = null;
            try
            {
                authority =
                    authorityStore.findAuthorityByURL(service.getServersByURL());
            }
            catch(MalformedURLException ex)
            {
                throw new RuntimeException(ex);
            }
        }
    }

    private void updateASNData()
    {
        makeBootstrapRequest(ASN_URL)
            .addCallback((ResponseEntity<JsonNode> entity) ->
            {
                parseBootstrapResults(entity.getBody());
            },
            (Throwable t) ->
            {
                System.out.println(t);
            });
    }
}
