package net.apnic.rdap.iana.scraper;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;

public class IANABootstrapFetcher
{
    private static final HttpHeaders REQUEST_HEADERS;
    public static final String BASE_URI_STR = "https://data.iana.org/rdap/";

    static {
        REQUEST_HEADERS = new HttpHeaders();
        REQUEST_HEADERS.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        REQUEST_HEADERS.add(HttpHeaders.USER_AGENT, "");
    }

    public enum RequestType
    {
        ASN("asn.json"),
        DOMAIN("dns.json"),
        IPv4("ipv4.json"),
        IPv6("ipv6.json");

        private final String fileName;

        RequestType(String fileName) {
            this.fileName = fileName;
        }

        public URI getRequestURI(String baseURI) {
            return URI.create(baseURI + fileName);
        }
    }

    private final RestTemplate restClient = new RestTemplate();
    private final String useBaseURI;

    public IANABootstrapFetcher() {
        this(BASE_URI_STR);
    }

    public IANABootstrapFetcher(String baseURI) {
        this.useBaseURI = baseURI;
    }


    public String getBaseUrl(){
        return this.useBaseURI;
    }
    public BootstrapResult makeRequestForType(RequestType requestType) {
        HttpEntity<?> entity = new HttpEntity<>(REQUEST_HEADERS);
        ResponseEntity<JsonNode> rEntity =
            restClient.exchange(requestType.getRequestURI(useBaseURI),
                HttpMethod.GET, entity, JsonNode.class);
        return BootstrapResultParser.parse(rEntity.getBody());
    }
}
