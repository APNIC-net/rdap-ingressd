package net.apnic.rdap.help.config;

import net.apnic.rdap.directory.Directory;
import net.apnic.rdap.help.filters.HelpRouteFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HelpConfiguration
{
    @Bean
    @Autowired
    public HelpRouteFilter helpRouteFilter(Directory directory)
    {
        return new HelpRouteFilter(directory);
    }
}
