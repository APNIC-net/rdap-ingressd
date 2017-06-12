package net.apnic.rdap.stats.scraper;

import java.io.InputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.Scanner;

import net.apnic.rdap.scraper.Scraper;
import net.apnic.rdap.stats.parser.DelegatedStatsException;
import net.apnic.rdap.stats.parser.DelegatedStatsParser;
import net.apnic.rdap.util.ConcurrentUtil;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;

public class DelegatedStatsScraper
    implements Scraper
{
    private enum SupportedScheme
    {
        HTTP("http"),
        HTTPS("https");

        private final String scheme;

        private SupportedScheme(String scheme)
        {
            this.scheme = scheme;
        }

        @Override
        public String toString()
        {
            return scheme;
        }
    }

    private static final String FTP_SCHEME = "ftp";
    private static final String HTTP_SCHEME = "http";
    private static final String HTTPS_SCHEME = "https";

    private HttpHeaders requestHeaders = null;
    private AsyncRestTemplate restClient = null;
    private SupportedScheme statsScheme = null;
    private URI statsURI = null;

    public DelegatedStatsScraper(URI statsURI)
    {
        try
        {
            SupportedScheme scheme =
                SupportedScheme.valueOf(statsURI.getScheme().toUpperCase());
            this.statsScheme = scheme;
        }
        catch(IllegalArgumentException ex)
        {
            throw new IllegalArgumentException("Non support scheme for URI");
        }

        this.restClient = new AsyncRestTemplate();
        this.statsURI = statsURI;
        setupRequestHeaders();
    }

    public DelegatedStatsScraper(String statsURI)
        throws URISyntaxException
    {
        this(new URI(statsURI));
    }

    private CompletableFuture<InputStream> makeDelegatedHttpRequest()
    {
        HttpEntity<Resource> entity = new HttpEntity<Resource>(requestHeaders);

        ListenableFuture<ResponseEntity<Resource>> lFuture =
            restClient.exchange(statsURI, HttpMethod.GET,
                                entity, Resource.class);

        return ConcurrentUtil.buildCompletableFuture(lFuture)
            .thenApply((ResponseEntity<Resource> responseEntity) ->
            {
                try
                {
                    return responseEntity.getBody().getInputStream();
                }
                catch(IOException ex)
                {
                    throw new RuntimeException(ex);
                }
            });
    }

    private void processStatsInput(InputStream iStream)
    {
        /*try
        {
            //statsParser.parse(iStream);
        }
        catch(DelegatedStatsException ex)
        {
        }*/
    }

    private void setupRequestHeaders()
    {
        requestHeaders = new HttpHeaders();
        requestHeaders.setAccept(Arrays.asList(MediaType.TEXT_PLAIN));
        requestHeaders.add(HttpHeaders.USER_AGENT, "");
    }

    public void start()
    {
        CompletableFuture<InputStream> request = null;

        if(statsScheme == SupportedScheme.HTTP ||
           statsScheme == SupportedScheme.HTTPS)
        {
            request = makeDelegatedHttpRequest();
        }

        request
            .thenAccept((InputStream iStream) ->
            {
                processStatsInput(iStream);
            });
    }
}
