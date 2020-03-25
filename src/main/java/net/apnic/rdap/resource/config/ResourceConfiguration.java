package net.apnic.rdap.resource.config;

import net.apnic.rdap.authority.RDAPAuthority;
import net.apnic.rdap.autnum.AutnumStatsResourceLocator;
import net.apnic.rdap.autnum.AsnRange;
import net.apnic.rdap.domain.Domain;
import net.apnic.rdap.domain.DomainStatsResourceLocator;
import net.apnic.rdap.entity.Entity;
import net.apnic.rdap.ip.IPStatsResourceLocator;
import net.apnic.rdap.nameserver.NameServer;
import net.apnic.rdap.resource.ResourceLocator;
import net.apnic.rdap.resource.StaticResourceLocator;
import net.apnic.rdap.resource.store.ResourceStorage;

import net.ripe.ipresource.IpRange;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    public ResourceStorage<AsnRange> autnumResourceStorage()
    {
        return autnumResourceLocator;
    }

    @Bean
    public ResourceLocator<Domain> domainResourceLocator()
    {
        return domainResourceLocator;
    }

    @Bean
    public ResourceStorage<Domain> domainResourceStorage()
    {
        return domainResourceLocator;
    }

    @Bean
    @Autowired
    public ResourceLocator<Entity> entityResourceLocator(
        @Qualifier("defaultAuthority") RDAPAuthority defaultAuthority)
    {
        return new StaticResourceLocator<Entity>(defaultAuthority);
    }

    @Bean
    @Autowired
    public ResourceLocator<Void> helpResourceLocator(
        @Qualifier("defaultAuthority") RDAPAuthority defaultAuthority)
    {
        return new StaticResourceLocator<Void>(defaultAuthority);
    }

    @Bean
    @Autowired
    public ResourceLocator<Void> historyResourceLocator(
            @Qualifier("defaultAuthority") RDAPAuthority defaultAuthority) {
        return new StaticResourceLocator<>(defaultAuthority);
    }

    @Bean
    public ResourceLocator<IpRange> ipResourceLocator()
    {
        return ipResourceLocator;
    }

    @Bean
    public ResourceStorage<IpRange> ipResourceStorage()
    {
        return ipResourceLocator;
    }

    @Bean
    @Autowired
    public ResourceLocator<NameServer> nsResourcelocator(
        @Qualifier("defaultAuthority") RDAPAuthority defaultAuthority)
    {
        return new StaticResourceLocator<NameServer>(defaultAuthority);
    }

    @Bean
    @Autowired
    public ResourceLocator<Void> searchPathLocator(
        @Qualifier("defaultAuthority") RDAPAuthority defaultAuthority)
    {
        return new StaticResourceLocator<Void>(defaultAuthority);
    }
}
