package net.apnic.rdap.filter;

import java.net.URI;
import java.util.Arrays;
import java.util.stream.Stream;

import org.hamcrest.collection.IsMapContaining;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import static org.junit.Assert.assertThat;
import org.junit.runner.RunWith;
import org.junit.Test;

import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CorsControllerTest
{
    @LocalServerPort
    private int serverPort;

    private Matcher<ResponseEntity<String>> isCorsResponse =
        Matchers.allOf(
            Matchers.hasProperty("headers",
                IsMapContaining.hasEntry("Access-Control-Allow-Origin",
                                         Arrays.asList("*"))));

    @Test
    public void returnsCorsHeadersGet()
        throws Exception
    {
        Stream<String> corsPaths = Stream.of("/ip/10.5.5.5/32",
                                             "/autnum/1234",
                                             "/entity/apnic",
                                             "/domain/cors.apnic.net",
                                             "/nameserver/ns1.apnic.net",
                                             "/help");

        corsPaths.forEach(corsPath -> {
            RequestEntity<Void> request;
            try {
                request = RequestEntity
                    .get(new URI("http://localhost:" + this.serverPort + corsPath))
                    .header("Origin", "https://cors.apnic.net")
                    .build();
            } catch(Exception ex) {
                throw new RuntimeException(ex);
            }

            ResponseEntity<String> response = new TestRestTemplate()
                .exchange(request, String.class);

            assertThat(response, isCorsResponse);
        });
    }

    @Test
    public void returnsCorsHeadersHead()
        throws Exception
    {
        Stream<String> corsPaths = Stream.of("/ip/10.5.5.5/32",
                                             "/autnum/1234",
                                             "/entity/apnic",
                                             "/domain/cors.apnic.net",
                                             "/nameserver/ns1.apnic.net",
                                             "/help");

        corsPaths.forEach(corsPath -> {
            RequestEntity<Void> request;
            try {
                request = RequestEntity
                    .head(new URI("http://localhost:" + this.serverPort + corsPath))
                    .header("Origin", "https://cors.apnic.net")
                    .build();
            } catch(Exception ex) {
                throw new RuntimeException(ex);
            }

            ResponseEntity<String> response = new TestRestTemplate()
                .exchange(request, String.class);

            assertThat(response, isCorsResponse);
        });
    }
}
