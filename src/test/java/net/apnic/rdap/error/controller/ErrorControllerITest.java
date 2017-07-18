package net.apnic.rdap.error.controller;

import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.junit.Test;

import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ErrorControllerITest
{
    @LocalServerPort
    private int serverPort;

    @Test
    public void returns400ForMalformedRequests()
    {
        Stream<String> badPaths = Stream.of("/ip/not a real ip address",
                                            "/ip/1.2.3", "/ip/1.2.3.4/99",
                                            "/ip/ffff:ffff:ffff::/2",
                                            "/ip/ffff:");

        badPaths.forEach(badPath ->
        {
            ResponseEntity<Void> response = new TestRestTemplate()
                .getForEntity("http://localhost:" + this.serverPort + badPath,
                              Void.class);

            assertEquals(badPath, 400, response.getStatusCodeValue());
        });
    }

    @Test
    public void returns404ForMalformedRequests()
    {
        Stream.of("/doesnotexist",
                  "/entity/doesnotexist",
                  "/domain/apnic.example",
                  "/nameserver/ns1.apnic.example")
        .forEach(nonPath ->
        {
            ResponseEntity<Void> response = new TestRestTemplate()
                .getForEntity("http://localhost:" + this.serverPort + nonPath,
                              Void.class);

            assertEquals(nonPath, 404, response.getStatusCodeValue());
        });
    }
}
