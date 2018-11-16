package net.apnic.rdap.scraper;

import net.apnic.rdap.autnum.AsnRange;
import net.apnic.rdap.domain.Domain;
import net.apnic.rdap.resource.ResourceMapping;
import net.ripe.ipresource.IpRange;

import java.util.List;
import java.util.Optional;

/**
 * Encapsulates the total data retrieved by a {@link Scraper}.
 */
public class ScraperResult {
    final private List<ResourceMapping<IpRange>> ipMappings;
    final private List<ResourceMapping<AsnRange>> asnMappings;
    final private List<ResourceMapping<Domain>> domainMappings;

    public ScraperResult(List<ResourceMapping<IpRange>> ipMappings,
                         List<ResourceMapping<AsnRange>> asnMappings,
                         List<ResourceMapping<Domain>> domainMappings) {
        this.ipMappings = ipMappings;
        this.asnMappings = asnMappings;
        this.domainMappings = domainMappings;
    }

    public Optional<List<ResourceMapping<IpRange>>> getIpMappings() {
        return Optional.ofNullable(ipMappings);
    }

    public Optional<List<ResourceMapping<AsnRange>>> getAsnMappings() {
        return Optional.ofNullable(asnMappings);
    }

    public Optional<List<ResourceMapping<Domain>>> getDomainMappings() {
        return Optional.ofNullable(domainMappings);
    }
}
