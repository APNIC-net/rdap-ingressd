package net.apnic.rdap.stats.scraper;

import net.apnic.rdap.authority.RDAPAuthorityStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration for {@link CustomStatsScraper}.
 */
@Configuration
@ConfigurationProperties(prefix = "rdap.scraping.scrapers.custom")
public class CustomStatsConfiguration {

    private List<CustomEntry> entries;

    @ConditionalOnProperty("rdap.scraping.scrapers.custom.enabled")
    @Bean("custom")
    @Autowired
    public CustomStatsScraper getCustomStatsScraper(RDAPAuthorityStore rdapAuthorityStore) {
        return new CustomStatsScraper(rdapAuthorityStore, entries);
    }

    public List<CustomEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<CustomEntry> entries) {
        this.entries = entries;
    }

}
