package net.apnic.rdap.rdap.config;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.apnic.rdap.rdap.RDAPLink;
import net.apnic.rdap.rdap.RDAPNotice;
import net.apnic.rdap.rdap.RDAPObjectFactory;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="rdap")
public class RDAPConfiguration
{
    public static class ConfigLink
    {
        private String href;
        private String rel;
        private String type;

        public String getHref()
        {
            return href;
        }

        public String getRel()
        {
            return rel;
        }

        public String getType()
        {
            return type;
        }

        public void setHref(String href)
        {
            this.href = href;
        }

        public void setRel(String rel)
        {
            this.rel = rel;
        }

        public void setType(String type)
        {
            this.type = type;
        }

        public RDAPLink toLink()
        {
            return new RDAPLink(href, rel, type, null);
        }
    }

    public static class ConfigNotice
    {
        private List<String> description;
        private String title;
        private List<ConfigLink> links = new ArrayList<ConfigLink>();

        public List<String> getDescription()
        {
            return description;
        }

        public List<ConfigLink> getLinks()
        {
            return links;
        }

        public String getTitle()
        {
            return title;
        }

        public void setDescription(List<String> description)
        {
            this.description = description;
        }

        public void setLinks(List<ConfigLink> links)
        {
            this.links = links;
        }

        public void setTitle(String title)
        {
            this.title = title;
        }

        public RDAPNotice toNotice()
        {
            return new RDAPNotice(description,
                links.stream().map(ConfigLink::toLink).collect(Collectors.toList()),
                title);
        }
    }

    private List<ConfigNotice> configNotices = new ArrayList<ConfigNotice>();
    private List<RDAPNotice> rdapNotices;

    public List<ConfigNotice> getNotices()
    {
        return configNotices;
    }

    @Bean
    public RDAPObjectFactory rdapObjectFactory()
    {
        RDAPObjectFactory factory = new RDAPObjectFactory(
            getNotices().stream().map(ConfigNotice::toNotice).collect(Collectors.toList()));
        return factory;
    }

    public void setNotices(List<ConfigNotice> notices)
    {
        this.configNotices = notices;
    }
}
