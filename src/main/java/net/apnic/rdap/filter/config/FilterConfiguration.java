package net.apnic.rdap.filter.config;

import net.apnic.rdap.filter.filters.RDAPPreFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 */
@Configuration
public class FilterConfiguration
{
    @Bean
    public RDAPPreFilter rdapPreFilter()
    {
        return new RDAPPreFilter();
    }
}
