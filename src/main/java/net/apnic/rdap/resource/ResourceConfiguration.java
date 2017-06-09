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
    private ResourceLocator<AsnRange> autnumResourceLocator =
        new AutnumStatsResourceLocator();

    private ResourceLocator<IpRange> ipResourceLocator =
        new IPStatsResourceLocator();

    private ResourceLocator<Domain> domainResourceLocator =
        new DomainStatsResourceLocator(ipResourceLocator);

    @Bean
    public ResourceLocator<AsnRange> autnumResourcelocator()
    {
        return autnumResourceLocator;
    }

    @Bean
    public ResourceLocator<Domain> domainResourceLocator()
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
    public ResourceLocator<NameServer> nsResourcelocator()
    {
        return new StaticResourceLocator<NameServer>();
    }
}
