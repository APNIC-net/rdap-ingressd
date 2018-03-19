package net.apnic.rdap.iana.scraper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.apnic.rdap.authority.RDAPAuthority;
import net.apnic.rdap.authority.RDAPAuthorityStore;
import net.apnic.rdap.autnum.AsnRange;
import net.apnic.rdap.domain.Domain;
import net.apnic.rdap.resource.store.ResourceStore;
import net.apnic.rdap.scraper.Scraper;

import net.ripe.ipresource.IpRange;

/**
 * Scraper for IANA bootstrap service.
 *
 * @see RFC 7484
 */
public class IANABootstrapScraper
    implements Scraper
{
    /**
     * Lambda callback signature for mapping of authorities on to discovered
     * bootstrap results.
     */
    private interface ResourceMapper
    {
        public void process(RDAPAuthority authority, BootstrapService service);
    }

    private static final Logger LOGGER =
        Logger.getLogger(IANABootstrapScraper.class.getName());

    private final IANABootstrapFetcher bootstrapFetcher;

    /**
     * Constructor for creating an IANA bootstrap scraper.
     */
    public IANABootstrapScraper()
    {
        this(new IANABootstrapFetcher());
    }

    public IANABootstrapScraper(IANABootstrapFetcher bootstrapFetcher)
    {
        this.bootstrapFetcher = bootstrapFetcher;
    }

    /**
     * {@inheritDocs}
     */
    @Override
    public String getName()
    {
        return "iana-bootstrap-scraper";
    }

    /**
     * Main scraper method that dispatches a one-time scrape of IANA.
     *
     * This method is triggered by a scraper scheduler.
     *
     * @see net.apnic.rdap.scraper.ScraperScheduler
     */
    @Override
    public CompletableFuture<Void> start(ResourceStore store,
                                         RDAPAuthorityStore authorityStore)
    {
        return CompletableFuture.allOf(updateASNData(store, authorityStore),
                                       updateDomainData(store, authorityStore),
                                       updateIPv4Data(store, authorityStore), 
                                       updateIPv6Data(store, authorityStore));
    }

    /**
     * Parses the results from any bootstrap request calling the provided
     * mapping
     */
    private void mapBootstrapResults(BootstrapResult result,
                                     RDAPAuthorityStore authorityStore,
                                     ResourceMapper mapper)
    {
        for(BootstrapService service : result.getServices())
        {
            List<URI> serviceURIs = null;

            try
            {
                serviceURIs = service.getServersByURI();
            }
            catch(URISyntaxException ex)
            {
                throw new RuntimeException(ex);
            }

            RDAPAuthority authority =
                authorityStore.findAuthorityByURI(serviceURIs);

            if(authority == null)
            {
                authority = authorityStore.createAnonymousAuthority();
                authority.addServers(serviceURIs);
            }

            try
            {
                mapper.process(authority, service);
            }
            catch(Exception ex)
            {
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * Drives the main update cycle for asn bootstrap results.
     *
     * @return Promise that's complete when an IANA asn update has
     * completed.
     */
    public CompletableFuture<Void> updateASNData(ResourceStore store,
                                                  RDAPAuthorityStore authorityStore)
    {
        return bootstrapFetcher.makeRequestForType(IANABootstrapFetcher.RequestType.ASN)
            .thenAccept((BootstrapResult result) ->
            {
                mapBootstrapResults(result, authorityStore,
                    (RDAPAuthority authority, BootstrapService service) ->
                    {
                        // We try to group IANA autnum delegations into large
                        // blocks for contiguous intervals. This is to allow
                        // smaller overlapping ranges from other scrapers.
                        AsnRange lastRange = null;
                        for(String strAsnRange : service.getResources())
                        {
                            AsnRange asnRange = AsnRange.parse(strAsnRange);
                            if(lastRange == null)
                            {
                                lastRange = asnRange;
                            }
                            else if(lastRange.isContiguousWith(asnRange))
                            {
                                lastRange = lastRange.makeContiguousWith(asnRange);
                            }
                            else
                            {
                                store.putAutnumMapping(lastRange, authority);
                                lastRange = asnRange;
                            }
                        }
                        // Emplace the last value into the map
                        if(lastRange != null)
                        {
                            store.putAutnumMapping(lastRange, authority);
                        }
                    });
            });
    }

    /**
     * Drives the main update cycle for domain bootstrap results
     *
     * @return Promise that's complete when an IANA domain update has
     * completed.
     */
    public CompletableFuture<Void> updateDomainData(ResourceStore store,
                                                     RDAPAuthorityStore authorityStore)
    {
        return bootstrapFetcher.makeRequestForType(IANABootstrapFetcher.RequestType.DOMAIN)
            .thenAccept((BootstrapResult result) ->
            {
                mapBootstrapResults(result, authorityStore,
                    (RDAPAuthority authority, BootstrapService service) ->
                    {
                        for(String tldDomain : service.getResources())
                        {
                            Domain domain = new Domain(tldDomain);
                            store.putDomainMapping(domain, authority);
                        }
                    });
            });
    }

    /**
     * Utility method that shares the same logic for driving all ip address
     * updates.
     *
     * @param ipBootstrapURI The URI for the ip bootstrap data to process
     * @return Promise that's complete when an IANA ip update has
     * completed
     */
    private CompletableFuture<Void> updateIPAllData(IANABootstrapFetcher.RequestType requestType,
                                                    ResourceStore store,
                                                    RDAPAuthorityStore authorityStore)
    {
        return bootstrapFetcher.makeRequestForType(requestType)
            .thenAccept((BootstrapResult result) ->
            {
                mapBootstrapResults(result, authorityStore,
                    (RDAPAuthority authority, BootstrapService service) ->
                    {
                        for(String strIpRange : service.getResources())
                        {
                            IpRange ipRange = IpRange.parse(strIpRange);
                            store.putIPMapping(ipRange, authority);
                        }
                    });
            });
    }

    /**
     * Drives the main update cycle for ipv4 bootstrap results.
     *
     * Proxies through to updateIPAllData()
     * @return Promise that's complete when an IANA ipv4 update has
     * complete.
     */
    public CompletableFuture<Void> updateIPv4Data(ResourceStore store,
                                                   RDAPAuthorityStore authorityStore)
    {
        return updateIPAllData(IANABootstrapFetcher.RequestType.IPv4, store, authorityStore);
    }

    /**
     * Drives the main update cycle for ipv6 bootstrap results.
     *
     * Proxies through to updateIPAllData()
     * @return Promise that's complete when an IANA ipv6 update has
     * complete.
     */
    public CompletableFuture<Void> updateIPv6Data(ResourceStore store,
                                                   RDAPAuthorityStore authorityStore)
    {
        return updateIPAllData(IANABootstrapFetcher.RequestType.IPv6, store, authorityStore);
    }
}
