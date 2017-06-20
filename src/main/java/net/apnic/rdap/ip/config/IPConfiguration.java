package net.apnic.rdap.ip.config;

import net.apnic.rdap.directory.Directory;
import net.apnic.rdap.ip.filters.IPRouteFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IPConfiguration
{
    @Bean
    @Autowired
    public IPRouteFilter ipRouteFilter(Directory directory)
    {
        return new IPRouteFilter(directory);
    }
}
