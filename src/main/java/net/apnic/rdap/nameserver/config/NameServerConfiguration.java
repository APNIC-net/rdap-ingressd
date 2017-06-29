package net.apnic.rdap.nameserver.config;

import net.apnic.rdap.directory.Directory;
import net.apnic.rdap.nameserver.filters.NameServerRouteFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NameServerConfiguration
{
    @Bean
    @Autowired
    public NameServerRouteFilter nameServerFilter(Directory directory)
    {
        return new NameServerRouteFilter(directory);
    }
}
