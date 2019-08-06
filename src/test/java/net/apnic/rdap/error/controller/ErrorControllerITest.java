package net.apnic.rdap.error.controller;

import com.jayway.jsonpath.matchers.JsonPathMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsMapContaining;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ErrorControllerITest
{
    @LocalServerPort
    private int serverPort;

    private Matcher<ResponseEntity<String>> isValidRDAPResponse =
        Matchers.allOf(
            Matchers.hasProperty("headers",
                IsMapContaining.hasEntry("Content-Type",
                                         Arrays.asList("application/rdap+json"))),
            Matchers.hasProperty("body", Matchers.allOf(
                JsonPathMatchers.isJson(),
                JsonPathMatchers.hasJsonPath("$.errorCode"),
                JsonPathMatchers.hasJsonPath("$.title"),
                JsonPathMatchers.hasJsonPath("$.rdapConformance", Matchers.contains("rdap_level_0"))
                )
            )
        );

    private Matcher<ResponseEntity<String>> is400RDAPErrorResponse =
        Matchers.allOf(
            Matchers.hasProperty("statusCodeValue", Matchers.is(400)),
            isValidRDAPResponse,
            Matchers.hasProperty("body", Matchers.allOf(
                JsonPathMatchers.hasJsonPath("$.errorCode", Matchers.is("400")),
                JsonPathMatchers.hasJsonPath("$.title", Matchers.is("Bad Request"))
                )
            )
        );

    private Matcher<ResponseEntity<String>> is404RDAPErrorResponse =
        Matchers.allOf(
            Matchers.hasProperty("statusCodeValue", Matchers.is(404)),
            isValidRDAPResponse,
            Matchers.hasProperty("body", Matchers.allOf(
                JsonPathMatchers.hasJsonPath("$.errorCode", Matchers.is("404")),
                JsonPathMatchers.hasJsonPath("$.title", Matchers.is("Not Found"))
                )
            )
        );


    @Test
    public void returns400ForMalformedRequests()
    {
        Stream<String> badPaths = Stream.of("/ip/not a real ip address",
                                            "/ip/1.2.3", "/ip/1.2.3.4/99",
                                            "/ip/101.0.0.0-101.0.0.255",
                                            "/ip/2001::-2001::ffff",
                                            "/ip/ffff:ffff:ffff::/2",
                                            "/ip/ffff:");

        badPaths.forEach(badPath ->
        {
            ResponseEntity<String> response = new TestRestTemplate()
                .getForEntity("http://localhost:" + this.serverPort + badPath,
                              String.class);

            assertThat(response, is400RDAPErrorResponse);
        });
    }

    @Test
    public void returns404ForMalformedRequests()
    {
        Stream.of("/entity/doesnotexist",
                  "/domain/apnic.example",
                  "/nameserver/ns1.apnic.example")
        .forEach(nonPath ->
        {
            ResponseEntity<String> response = new TestRestTemplate()
                .getForEntity("http://localhost:" + this.serverPort + nonPath,
                              String.class);

            assertThat(response, is404RDAPErrorResponse);
        });
    }
}
