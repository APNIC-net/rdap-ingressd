package net.apnic.rdap.client;

import java.util.Arrays;
import java.util.List;

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
 *
 * This client currently returns the exact result recieved from an RDAP
 * service. i.e it will not follow redirects but return the exact response.
 */
public class RDAPClient
{
    /** Media type for RDAP responses */
    public static final MediaType RDAP_MEDIA_TYPE =
        new MediaType("application", "rdap+json");

    /** Supported media response types supported by this client */
    public static final List<MediaType> SUPPORTED_MEDIA_TYPES =
        Arrays.asList(RDAP_MEDIA_TYPE, MediaType.APPLICATION_JSON);

    /** Set of headers sent with every RDAP request */
    private final HttpHeaders staticHeaders;

    /** Spring async HTTP client for requests */
    private AsyncRestTemplate restClient;

    /** The RDAP server to perform requests against */
    private String rdapServer;

    /**
     * Constructs a new RDAPClient which will issues requests against the
     * supplied rdap server URI.
     *
     * @param rdapServer URI of the RDAP server this client uses.
     */
    public RDAPClient(String rdapServer)
    {
        this.staticHeaders = createStaticHeaders();
        this.restClient = createRestClient();
        this.rdapServer = rdapServer;
    }

    /**
     * Creates the Spring rest client used for RDAP requests.
     *
     * @return AsyncRestTemplate used for RDAP HTTP requests.
     */
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

    /**
     * Creates the static headers used for all RDAP requests.
     *
     * @Return Statis HTTP headers.
     */
    private static HttpHeaders createStaticHeaders()
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(RDAP_MEDIA_TYPE);

        String acceptTypes = MediaType.toString(SUPPORTED_MEDIA_TYPES);
        headers.add(HttpHeaders.ACCEPT, acceptTypes);

        return headers;
    }

    /**
     * Provides the RDAP server this client is using.
     *
     * @return String URI of the RDAP server in use by this client.
     */
    public final String getRDAPServer()
    {
        return this.rdapServer;
    }

    /**
     * Executes an autnum RDAP HEAD request.
     *
     * Head requests in RDAP are used to confirm the existance of an object
     * and get the HTTP headers from the request back.
     *
     * @param autnum AS number to check exists on the RDAP server.
     * @return Future containing the RDAP servers response.
     */
    public ListenableFuture<ResponseEntity<Void>>
        executeAutnumList(String autnum)
    {
        return performListQuery("/autnum/" + autnum);
    }

    /**
     * Executes a domain RDAP HEAD request.
     *
     * Head requests in RDAP are used to confirm the existance of an object
     * and get the HTTP headers from the request back.
     *
     * @param domain Domain name to check exists on the RDAP server.
     * @return Future containing the RDAP servers response.
     */
    public ListenableFuture<ResponseEntity<Void>>
        executeDomainList(String domain)
    {
        return performListQuery("/domain/" + domain);
    }

    /**
     * Executes an entity RDAP HEAD request.
     *
     * Head requests in RDAP are used to confirm the existance of an object
     * and get the HTTP headers from the request back.
     *
     * @param entity Entity to check exists on the RDAP server.
     * @return Future containing the RDAP servers response.
     */
    public ListenableFuture<ResponseEntity<Void>>
        executeEntityList(String entity)
    {
        return performListQuery("/entity/" + entity);
    }

    /**
     * Executes an ip RDAP HEAD request.
     *
     * Head requests in RDAP are used to confirm the existance of an object
     * and get the HTTP headers from the request back.
     *
     * @param ipAddress IP address to check exists on the RDAP server.
     * @return Future containing the RDAP servers response.
     */
    public ListenableFuture<ResponseEntity<Void>>
        executeIPList(String ipAddress)
    {
        return performListQuery("/ip/" + ipAddress);
    }

    /**
     * Executes a name server RDAP HEAD request.
     *
     * Head requests in RDAP are used to confirm the existance of an object
     * and get the HTTP headers from the request back.
     *
     * @param nameserver Name server to check exists on the RDAP server.
     * @return Future containing the RDAP servers response.
     */
    public ListenableFuture<ResponseEntity<Void>>
        executeNameserverList(String nameserver)
    {
        return performListQuery("/nameserver/" + nameserver);
    }

    /**
     * Executes an autnum RDAP GET request.
     *
     * Raw queries do not structure the return body of the request into a
     * usable POJO and return the server response as a binary blob.
     *
     * @param autnum AS number to query from the RDAP server.
     * @return Future containing the RDAP servers response.
     */
    public ListenableFuture<ResponseEntity<byte[]>>
        executeRawAutnumQuery(String autnum)
    {
        return performRawGetQuery("/autnum/" + autnum);
    }

    /**
     * Executes a domain RDAP GET request.
     *
     * Raw queries do not structure the return body of the request into a
     * usable POJO and return the server response as a binary blob.
     *
     * @param domain Domain name to query from the RDAP server.
     * @return Future containing the RDAP servers response.
     */
    public ListenableFuture<ResponseEntity<byte[]>>
        executeRawDomainQuery(String domain)
    {
        return performRawGetQuery("/domain/" + domain);
    }

    /**
     * Executes an entity RDAP GET request.
     *
     * Raw queries do not structure the return body of the request into a
     * usable POJO and return the server response as a binary blob.
     *
     * @param entity Entity to query from the RDAP server.
     * @return Future containing the RDAP servers response.
     */
    public ListenableFuture<ResponseEntity<byte[]>>
        executeRawEntityQuery(String entity)
    {
        return performRawGetQuery("/entity/" + entity);
    }

    /**
     * Executes a help RDAP request.
     *
     * Raw queries do not structure the return body of the request into a
     * usable POJO and return the server response as a binary blob.
     *
     * @return Future containing the RDAP servers response.
     */
    public ListenableFuture<ResponseEntity<byte[]>>
        executeRawHelpQuery()
    {
        return performRawGetQuery("/help");
    }

    /**
     * Executes an ip RDAP GET request.
     *
     * Raw queries do not structure the return body of the request into a
     * usable POJO and return the server response as a binary blob.
     *
     * @param ip IP address to query from the RDAP server.
     * @return Future containing the RDAP servers response.
     */
    public ListenableFuture<ResponseEntity<byte[]>>
        executeRawIPQuery(String ipAddress)
    {
        return performRawGetQuery("/ip/" + ipAddress);
    }

    /**
     * Executes a nameserver RDAP GET request.
     *
     * Raw queries do not structure the return body of the request into a
     * usable POJO and return the server response as a binary blob.
     *
     * @param nameserver Name server to query from the RDAP server.
     * @return Future containing the RDAP servers response.
     */
    public ListenableFuture<ResponseEntity<byte[]>>
        executeRawNameserverQuery(String nameserver)
    {
        return performRawGetQuery("/nameserver/" + nameserver);
    }

    /**
     * Performs an RDAP HEAD request for the given path segment.
     *
     * Convience function for performQuery().
     *
     * @param pathSegment The RDAP path segment to query.
     * @return Future containing the RDAP servers response.
     */
    private ListenableFuture<ResponseEntity<Void>>
        performListQuery(String pathSegment)
    {
        return performQuery(pathSegment, HttpMethod.HEAD, Void.class);
    }

    /**
     * Performs an RDAP GET request for the given path segment.
     *
     * Raw queries do not structure the return body of the request into a
     * usable POJO and return the server response as a binary blob.
     *
     * Convience function for performQuery().
     *
     * @param pathSegment The RDAP path segment to query.
     * @return Future containing the RDAP servers response.
     */
    private ListenableFuture<ResponseEntity<byte[]>>
        performRawGetQuery(String pathSegment)
    {
        return performQuery(pathSegment, HttpMethod.GET, byte[].class);
    }

    /**
     * Performs an RDAP query against the this clients server.
     *
     * Method allows for specifying the HTTP method, path segment and return
     * data type of the request.
     *
     * @param pathSegment The RDAP path segment to query.
     * @param method HTTP method to use for the RDAP query.
     * @param type Return type for body data.
     * @return Future containing the RDAP servers response.
     */
    private <T> ListenableFuture<ResponseEntity<T>>
        performQuery(String pathSegment, HttpMethod method, Class<T> type)
    {
        HttpEntity<?> entity = new HttpEntity(staticHeaders);
        return restClient.exchange(rdapServer + pathSegment,
                                   method, entity,
                                   type);
    }
}
