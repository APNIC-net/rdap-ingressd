package net.apnic.rdap.domain.config;

import net.apnic.rdap.domain.filters.DomainRouteFilter;
import net.apnic.rdap.domain.filters.DomainsRouteFilter;
import net.apnic.rdap.directory.Directory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfiguration
{
    @Bean
    @Autowired
    public DomainRouteFilter domainRouteFilter(Directory directory)
    {
        return new DomainRouteFilter(directory);
    }

    @Bean
    @Autowired
    public DomainsRouteFilter domainsRouteFilter(Directory directory)
    {
        return new DomainsRouteFilter(directory);
    }
}
