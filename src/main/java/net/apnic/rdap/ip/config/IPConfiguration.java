package net.apnic.rdap.ip.config;

import net.apnic.rdap.ip.filters.IPRouteFilter;
import net.apnic.rdap.resource.ResourceLocator;

import net.ripe.ipresource.IpRange;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IPConfiguration
{
    @Bean
    @Autowired
    public IPRouteFilter ipRouteFilter(ResourceLocator<IpRange> ipLocator)
    {
        return new IPRouteFilter(ipLocator);
    }
}
