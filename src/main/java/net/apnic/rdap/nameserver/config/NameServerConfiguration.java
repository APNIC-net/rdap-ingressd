package net.apnic.rdap.nameserver.config;

import net.apnic.rdap.directory.Directory;
import net.apnic.rdap.nameserver.filters.NameServerRouteFilter;
import net.apnic.rdap.nameserver.filters.NameServersRouteFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NameServerConfiguration
{
    @Bean
    @Autowired
    public NameServerRouteFilter nameServerRouteFilter(Directory directory)
    {
        return new NameServerRouteFilter(directory);
    }

    @Bean
    @Autowired
    public NameServersRouteFilter nameServersRouteFilter(Directory directory)
    {
        return new NameServersRouteFilter(directory);
    }
}
