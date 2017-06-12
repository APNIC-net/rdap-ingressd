package net.apnic.rdap.resource;

import net.apnic.rdap.autnum.AutnumStatsResourceLocator;
import net.apnic.rdap.autnum.AsnRange;
import net.apnic.rdap.domain.Domain;
import net.apnic.rdap.domain.DomainStatsResourceLocator;
import net.apnic.rdap.entity.Entity;
import net.apnic.rdap.ip.IPStatsResourceLocator;
import net.apnic.rdap.nameserver.NameServer;

import net.ripe.ipresource.IpRange;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResourceConfiguration
{
    private AutnumStatsResourceLocator autnumResourceLocator =
        new AutnumStatsResourceLocator();

    private IPStatsResourceLocator ipResourceLocator =
        new IPStatsResourceLocator();

    private DomainStatsResourceLocator domainResourceLocator =
        new DomainStatsResourceLocator(ipResourceLocator);

    @Bean
    public ResourceLocator<AsnRange> autnumResourcelocator()
    {
        return autnumResourceLocator;
    }

    @Bean
    public ResourceStore<AsnRange> autnumResourceStore()
    {
        return autnumResourceLocator;
    }

    @Bean
    public ResourceLocator<Domain> domainResourceLocator()
    {
        return domainResourceLocator;
    }

    @Bean
    public ResourceStore<Domain> domainResourceStore()
    {
        return domainResourceLocator;
    }

    @Bean
    public ResourceLocator<Entity> entityResourceLocator()
    {
        return new StaticResourceLocator<Entity>();
    }

    @Bean
    public ResourceLocator<IpRange> ipResourceLocator()
    {
        return ipResourceLocator;
    }

    @Bean
    public ResourceStore<IpRange> ipResourceStore()
    {
        return ipResourceLocator;
    }

    @Bean
    public ResourceLocator<NameServer> nsResourcelocator()
    {
        return new StaticResourceLocator<NameServer>();
    }
}
