package net.apnic.rdap.iana.scraper;

import net.apnic.rdap.authority.RDAPAuthority;
import net.apnic.rdap.authority.RDAPAuthorityStore;
import net.apnic.rdap.autnum.AsnRange;
import net.apnic.rdap.domain.Domain;
import net.apnic.rdap.resource.ResourceMapping;
import net.apnic.rdap.scraper.Scraper;
import net.apnic.rdap.scraper.ScraperException;
import net.apnic.rdap.scraper.ScraperResult;
import net.ripe.ipresource.IpRange;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Scraper for IANA bootstrap service.
 *
 * See details on RFC 7484.
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
        void process(RDAPAuthority authority, BootstrapService service);
    }

    private final RDAPAuthorityStore rdapAuthorityStore;
    private final IANABootstrapFetcher bootstrapFetcher;

    private final Logger LOGGER =
            Logger.getLogger(IANABootstrapScraper.class.getName());


    /**
     * Constructor for creating an IANA bootstrap scraper.
     * @param rdapAuthorityStore instance of {@link RDAPAuthorityStore} to retrieve {@link RDAPAuthority} data
     */
    public IANABootstrapScraper(RDAPAuthorityStore rdapAuthorityStore)
    {
        this(rdapAuthorityStore, new IANABootstrapFetcher());
    }

    public IANABootstrapScraper(RDAPAuthorityStore rdapAuthorityStore, IANABootstrapFetcher bootstrapFetcher)
    {
        this.rdapAuthorityStore = rdapAuthorityStore;
        this.bootstrapFetcher = bootstrapFetcher;
    }

    @Override
    public String getName()
    {
        return "iana-bootstrap-scraper";
    }

    @Override
    public ScraperResult fetchData() throws ScraperException {
        try {
            LOGGER.info("IANA base url: " + bootstrapFetcher.getBaseUrl());
            return new ScraperResult(fetchIPData(), fetchASNData(), fetchDomainData());
        } catch (Exception e) {
            throw new ScraperException("Error retrieving IANA data.", e);
        }
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
                authority = RDAPAuthority.createAnonymousAuthority();
                authority.setIanaBootstrapRefServers(serviceURIs);
                rdapAuthorityStore.addAuthority(authority);
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

    private List<ResourceMapping<AsnRange>> fetchASNData() {
        // IANA's ASN bootstrap file contains delegations from IANA to the
        // RIRs. These delegations may be contiguous (i.e. unaggregated):
        // e.g., APNIC has separate delegations for 131072-132095 and
        // 132096-133119.  Other scrapers may aggregate these delegations
        // together, and if those other scrapers are run after the IANA
        // scraper, then they won't be able to add the delegation information
        // to the ResourceLocator, because it will overlap (not be fully
        // contained within) an existing delegation.  To avoid this,
        // aggregate IANA ASN delegations where possible.
        List<ResourceMapping<AsnRange>> mappings = new ArrayList<>();
        BootstrapResult result =  bootstrapFetcher.makeRequestForType(IANABootstrapFetcher.RequestType.ASN);
        mapBootstrapResults(result, rdapAuthorityStore, (RDAPAuthority authority, BootstrapService service) -> {
                // We try to group IANA autnum delegations into large
                // blocks for contiguous intervals. This is to allow
                // smaller overlapping ranges from other scrapers.
                AsnRange lastRange = null;
                for (String strAsnRange : service.getResources()) {
                    AsnRange asnRange = AsnRange.parse(strAsnRange);
                    if (lastRange == null) {
                        lastRange = asnRange;
                    } else if (lastRange.isContiguousWith(asnRange)) {
                        lastRange = lastRange.makeContiguousWith(asnRange);
                    } else {
                        mappings.add(new ResourceMapping<>(lastRange, authority));
                        lastRange = asnRange;
                    }
                }
                // Insert the last value into the map
                if (lastRange != null) {
                    mappings.add(new ResourceMapping<>(lastRange, authority));
                }
        });

        return mappings;
    }

    private List<ResourceMapping<Domain>> fetchDomainData() {
        List<ResourceMapping<Domain>> mappings = new ArrayList<>();
        BootstrapResult result =  bootstrapFetcher.makeRequestForType(IANABootstrapFetcher.RequestType.DOMAIN);
        mapBootstrapResults(result, rdapAuthorityStore, (RDAPAuthority authority, BootstrapService service) -> {
            for (String tldDomain : service.getResources()) {
                Domain domain = new Domain(tldDomain);
                mappings.add(new ResourceMapping<>(domain, authority));
            }
        });

        return mappings;
    }

    private List<ResourceMapping<IpRange>> fetchIPDataPerType(IANABootstrapFetcher.RequestType requestType) {
        List<ResourceMapping<IpRange>> mappings = new ArrayList<>();
        BootstrapResult result = bootstrapFetcher.makeRequestForType(requestType);
        mapBootstrapResults(result, rdapAuthorityStore, (RDAPAuthority authority, BootstrapService service) -> {
            for(String strIpRange : service.getResources()) {
                mappings.add(new ResourceMapping<>(IpRange.parse(strIpRange), authority));
            }
        });
        return mappings;
    }

    private List<ResourceMapping<IpRange>> fetchIPData() {
        return Stream.concat(
                fetchIPDataPerType(IANABootstrapFetcher.RequestType.IPv4).stream(),
                fetchIPDataPerType(IANABootstrapFetcher.RequestType.IPv6).stream()
        ).collect(Collectors.toList());
    }
}
