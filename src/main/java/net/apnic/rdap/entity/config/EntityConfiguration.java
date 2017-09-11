package net.apnic.rdap.entity.config;

import net.apnic.rdap.directory.Directory;
import net.apnic.rdap.entity.filters.EntitiesRouteFilter;
import net.apnic.rdap.entity.filters.EntityRouteFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EntityConfiguration
{
    @Bean
    @Autowired
    public EntitiesRouteFilter entitiesRouteFilter(Directory directory)
    {
        return new EntitiesRouteFilter(directory);
    }

    @Bean
    @Autowired
    public EntityRouteFilter entityRouteFilter(Directory directory)
    {
        return new EntityRouteFilter(directory);
    }
}
