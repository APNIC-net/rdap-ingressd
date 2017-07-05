package net.apnic.rdap.autnum.config;

import net.apnic.rdap.autnum.filters.AutnumRouteFilter;
import net.apnic.rdap.directory.Directory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AutnumConfiguration
{
    @Bean
    @Autowired
    public AutnumRouteFilter autnumRouteFilter(Directory directory)
    {
        return new AutnumRouteFilter(directory);
    }
}
