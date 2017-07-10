package net.apnic.rdap.rdap.config;

import java.util.List;

import net.apnic.rdap.rdap.RDAPNotice;
import net.apnic.rdap.rdap.RDAPObjectFactory;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="rdap")
public class RDAPConfiguration
{
    private List<RDAPNotice> notices = null;

    public List<RDAPNotice> getNotices()
    {
        return notices;
    }

    @Bean
    public RDAPObjectFactory rdapObjectFactory()
    {
        RDAPObjectFactory factory = new RDAPObjectFactory();
        factory.setDefaultNotices(notices);
        return factory;
    }

    public void setNotices(List<RDAPNotice> notices)
    {
        this.notices = notices;
    }
}
