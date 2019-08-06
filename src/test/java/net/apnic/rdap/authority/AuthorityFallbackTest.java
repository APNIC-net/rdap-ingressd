package net.apnic.rdap.authority;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "fallback")
@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// For some reason the next annotation is necessary due to spring testing ignoring that we are using a different profile
// and not using a different context from the other tests. Class mode "BEFORE_CLASS" also didn't work.
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AuthorityFallbackTest {

    @LocalServerPort
    private int port;

    private static ClientAndServer customDelegatedStatsProvider;
    private static ClientAndServer notFoundServer;
    private static ClientAndServer fallbackProxyServer;
    private static final String FALLBACK_PROXY_RETURN_MSG = "It works";
    private static final String REDIRECT_AUTHORITY_IPV4 = "1.0.0.0";
    private static final String PROXY_AUTHORITY_IPV4 = "2.0.0.0";
    private static final String FALLBACK_REDIRECT_AUTHORITY = "fallback_redirect";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private RDAPAuthorityStore rdapAuthorityStore;

    @BeforeClass
    public static void setup() throws IOException {
        customDelegatedStatsProvider = ClientAndServer.startClientAndServer(0);
        customDelegatedStatsProvider.when(HttpRequest.request()).respond(HttpResponse.response()
                .withStatusCode(HttpStatus.SC_OK)
                .withBody(IOUtils.toString(
                        ClientAndServer.class.getClassLoader().getResourceAsStream("stats/fallback_stats")))
        );
        System.setProperty("custom_scraper_port", customDelegatedStatsProvider.getLocalPort().toString());

        notFoundServer = ClientAndServer.startClientAndServer(0);
        notFoundServer.when(HttpRequest.request()).respond(HttpResponse.response()
                .withStatusCode(HttpStatus.SC_NOT_FOUND)
        );
        System.setProperty("404_port", notFoundServer.getLocalPort().toString());

        fallbackProxyServer = ClientAndServer.startClientAndServer(0);
        fallbackProxyServer.when(HttpRequest.request()).respond(HttpResponse.response()
                .withStatusCode(HttpStatus.SC_OK)
                .withBody(FALLBACK_PROXY_RETURN_MSG)
        );
        System.setProperty("fallback_proxy_port", fallbackProxyServer.getLocalPort().toString());
    }

    @AfterClass
    public static void tearDown() {
        customDelegatedStatsProvider.stop();
        notFoundServer.stop();
        fallbackProxyServer.stop();
    }

    @Test
    public void redirectFallbackTest() {
        // given the fallback configuration defined on "src/test/stats/fallback" and the mock servers
        // defined in setup()
        RDAPAuthority fallbackRedirectAuthority = rdapAuthorityStore.findAuthority(FALLBACK_REDIRECT_AUTHORITY);

        // when we query for redirected authority that returns 404
        ResponseEntity<String> response = testRestTemplate.exchange(
                "http://localhost:" + port + "/ip/" + REDIRECT_AUTHORITY_IPV4,
                HttpMethod.GET,
                null,
                String.class);

        // then
        assertThat(response.getStatusCode(), is(org.springframework.http.HttpStatus.MOVED_PERMANENTLY));
        assertThat(response.getHeaders().get("Location").get(0),
                is(fallbackRedirectAuthority.getRoutingTarget().resolve("ip/" + REDIRECT_AUTHORITY_IPV4).toString()));
    }

    @Test
    public void proxyFallbackTest() {
        // given the fallback configuration defined on "src/test/stats/fallback" and the mock servers
        // defined in setup()

        // when we query for redirected authority that returns 404
        ResponseEntity<String> response = testRestTemplate.exchange(
                "http://localhost:" + port + "/ip/" + PROXY_AUTHORITY_IPV4,
                HttpMethod.GET,
                null,
                String.class);

        // then
        assertThat(response.getStatusCode(), is(org.springframework.http.HttpStatus.OK));
        assertThat(response.getBody(), is(FALLBACK_PROXY_RETURN_MSG));
    }
}
