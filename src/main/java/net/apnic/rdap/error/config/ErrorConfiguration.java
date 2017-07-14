package net.apnic.rdap.error.config;

import net.apnic.rdap.error.filters.ZuulErrorFilter;
import net.apnic.rdap.rdap.RDAPObjectFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ErrorConfiguration
{
    @Bean
    @Autowired
    public ZuulErrorFilter zuulErrorFilter(RDAPObjectFactory factory)
    {
        return new ZuulErrorFilter(factory);
    }
}
