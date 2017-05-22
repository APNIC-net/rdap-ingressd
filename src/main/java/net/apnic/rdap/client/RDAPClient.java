package net.apnic.rdap.client;

import java.util.Arrays;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;

import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * RDAP client for initiating requests against an RDAP service.
 */
public class RDAPClient
{
    private static final MediaType RDAP_MEDIA_TYPE =
        new MediaType("application", "rdap+json");

    private final HttpHeaders staticHeaders;
    private AsyncRestTemplate restClient;
    private String rdapServer;

    public RDAPClient(String rdapServer)
    {
        this.staticHeaders = createStaticHeaders();
        this.restClient = createRestClient();
        this.rdapServer = rdapServer;
    }

    public final String getRDAPServer()
    {
        return this.rdapServer;
    }

    private static HttpHeaders createStaticHeaders()
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(RDAP_MEDIA_TYPE);
        headers.add(HttpHeaders.ACCEPT,
                    MediaType.toString(Arrays.asList(RDAP_MEDIA_TYPE)));
        return headers;
    }

    public ListenableFuture<ResponseEntity<byte[]>>
        executeRawAutnumQuery(String autnum)
    {
        return performQuery("/autnum/" + autnum, byte[].class);
    }

    public ListenableFuture<ResponseEntity<byte[]>>
        executeRawDomainQuery(String domain)
    {
        return performQuery("/domain/" + domain, byte[].class);
    }

    public ListenableFuture<ResponseEntity<byte[]>>
        executeRawEntityQuery(String entity)
    {
        return performQuery("/entity/" + entity, byte[].class);
    }

    public ListenableFuture<ResponseEntity<byte[]>>
        executeRawHelpQuery()
    {
        return performQuery("/help", byte[].class);
    }

    public ListenableFuture<ResponseEntity<byte[]>>
        executeRawIPQuery(String ipAddress)
    {
        return performQuery("/ip/" + ipAddress, byte[].class);
    }

    public ListenableFuture<ResponseEntity<byte[]>>
        executeRawNameserverQuery(String nameserver)
    {
        return performQuery("/nameserver/" + nameserver, byte[].class);
    }

    private <T> ListenableFuture<ResponseEntity<T>>
        performQuery(String pathSegment, Class<T> type)
    {
        HttpEntity<?> entity = new HttpEntity(staticHeaders);
        return restClient.exchange(rdapServer + pathSegment,
                                   HttpMethod.GET, entity,
                                   type);
    }

    private static AsyncRestTemplate createRestClient()
    {
        CloseableHttpAsyncClient client = HttpAsyncClientBuilder.create()
            .setConnectionReuseStrategy((ign1, ign2) -> true)
            .setConnectionManagerShared(false)
            .setRedirectStrategy(new RDAPClientRedirectStrategy())
            .setUserAgent("rdap-ingressd")
            .build();

        HttpComponentsAsyncClientHttpRequestFactory reqFactory =
            new HttpComponentsAsyncClientHttpRequestFactory();
        reqFactory.setHttpAsyncClient(client);

        AsyncRestTemplate restClient = new AsyncRestTemplate(reqFactory);
        restClient.setErrorHandler(new RDAPClientErrorHandler());
        return restClient;
    }
}
