package net.apnic.rdap.iana.config;

import net.apnic.rdap.authority.RDAPAuthorityStore;
import net.apnic.rdap.autnum.AsnRange;
import net.apnic.rdap.domain.Domain;
import net.apnic.rdap.iana.scraper.IANABootstrapScraper;
import net.apnic.rdap.resource.ResourceStore;

import net.ripe.ipresource.IpRange;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration singleton for IANA scraping activities.
 */
@Configuration
public class IANAConfiguration
{
    @ConditionalOnProperty(value="rdap.scraping.scrapers.iana.enabled")
    @Bean(value="iana")
    @Autowired
    public IANABootstrapScraper ianaScraper(RDAPAuthorityStore authorityStore,
                                            ResourceStore<AsnRange> autnumStore,
                                            ResourceStore<Domain> domainStore,
                                            ResourceStore<IpRange> ipStore)
    {
        return new IANABootstrapScraper(authorityStore, autnumStore,
                                        domainStore, ipStore);
    }
}
