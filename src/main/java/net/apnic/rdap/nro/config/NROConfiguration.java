package net.apnic.rdap.nro.config;

import net.apnic.rdap.autnum.AsnRange;
import net.apnic.rdap.authority.RDAPAuthorityStore;
import net.apnic.rdap.nro.scraper.NROScraper;
import net.apnic.rdap.resource.ResourceStore;

import net.ripe.ipresource.IpRange;

import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration singleton for NRO scraping activities.
 */
@Configuration
public class NROConfiguration
{
    @ConditionalOnProperty(value="rdap.scraping.scrapers.iana.enabled")
    @Bean(value="nro")
    @Autowired
    public NROScraper nroScraper(RDAPAuthorityStore authorityStore,
                                 ResourceStore<AsnRange> asnStore,
                                 ResourceStore<IpRange> ipStore)
    {
        return new NROScraper(authorityStore, asnStore, ipStore);
    }
}
