package net.apnic.rdap;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.verify.VerificationTimes;
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
import java.util.Objects;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "proxytest")
@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class RDAPProxyTest {

    @LocalServerPort
    private int port;

    private static ClientAndServer customDelegatedStatsProvider;
    private static ClientAndServer proxyServer;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @BeforeClass
    public static void setup() throws IOException {
        customDelegatedStatsProvider = ClientAndServer.startClientAndServer(0);
        customDelegatedStatsProvider.when(HttpRequest.request()).respond(HttpResponse.response()
                .withStatusCode(HttpStatus.SC_OK)
                .withBody(IOUtils.toString(
                        Objects.requireNonNull(
                                ClientAndServer.class.getClassLoader().getResourceAsStream("stats/proxytest_stats"))))
        );
        System.setProperty("custom_scraper_port", customDelegatedStatsProvider.getLocalPort().toString());

        proxyServer = ClientAndServer.startClientAndServer(0);
        System.setProperty("proxy_port", proxyServer.getLocalPort().toString());
    }

    @AfterClass
    public static void tearDown() {
        customDelegatedStatsProvider.stop();
        proxyServer.stop();
    }

    @After
    public void resetProxyMock() {
        proxyServer.reset();
    }

    @Test
    public void ipProxyTest() {
        // given the stats configuration defined on "src/test/stats/proxytest_stats" and the mock servers
        // defined in setup()
        final String requestPath = "/ip/1.0.0.0/8";
        proxyServer.when(HttpRequest.request()).respond(HttpResponse.response().withStatusCode(HttpStatus.SC_OK));

        // when
        ResponseEntity<String> response = testRestTemplate.exchange(
                "http://127.0.0.1:" + port + requestPath,
                HttpMethod.GET,
                null,
                String.class);

        // then
        proxyServer.verify(HttpRequest.request().withPath(requestPath), VerificationTimes.once());
        assertThat(response.getStatusCode().value(), is(HttpStatus.SC_OK));
    }

    @Test
    public void entitiesProxyTest() {
        // given the stats configuration defined on "src/test/stats/proxytest_stats" and the mock servers
        // defined in setup()
        final String requestPath = "/entities?handle=TH*";
        proxyServer.when(HttpRequest.request()).respond(HttpResponse.response().withStatusCode(HttpStatus.SC_OK));

        // when
        ResponseEntity<String> response = testRestTemplate.exchange(
                "http://127.0.0.1:" + port + requestPath,
                HttpMethod.GET,
                null,
                String.class);

        // then
        proxyServer.verify(HttpRequest.request().withPath("/entities").withQueryStringParameter("handle", "TH*"),
                VerificationTimes.once());
        assertThat(response.getStatusCode().value(), is(HttpStatus.SC_OK));
    }

    @Test
    public void domainsProxyTest() {
        // given the stats configuration defined on "src/test/stats/proxytest_stats" and the mock servers
        // defined in setup()
        final String requestPath = "/domains?name=202*";
        proxyServer.when(HttpRequest.request()).respond(HttpResponse.response().withStatusCode(HttpStatus.SC_OK));

        // when
        ResponseEntity<String> response = testRestTemplate.exchange(
                "http://127.0.0.1:" + port + requestPath,
                HttpMethod.GET,
                null,
                String.class);

        // then
        proxyServer.verify(HttpRequest.request().withPath("/domains").withQueryStringParameter("name", "202*"),
                VerificationTimes.once());
        assertThat(response.getStatusCode().value(), is(HttpStatus.SC_OK));
    }

    @Test
    public void historyProxyTest() {
        // given the stats configuration defined on "src/test/stats/proxytest_stats" and the mock servers
        // defined in setup()
        final String requestPath = "/history/ip/43.0.0.0/14";
        proxyServer.when(HttpRequest.request()).respond(HttpResponse.response().withStatusCode(HttpStatus.SC_OK));

        // when
        ResponseEntity<String> response = testRestTemplate.exchange(
                "http://127.0.0.1:" + port + requestPath,
                HttpMethod.GET,
                null,
                String.class);

        // then
        proxyServer.verify(HttpRequest.request().withPath(requestPath), VerificationTimes.once());
        assertThat(response.getStatusCode().value(), is(HttpStatus.SC_OK));
    }
}