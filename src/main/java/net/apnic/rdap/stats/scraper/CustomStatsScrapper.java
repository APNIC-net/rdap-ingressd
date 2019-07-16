package net.apnic.rdap.stats.scraper;

import net.apnic.rdap.authority.RDAPAuthorityStore;
import net.apnic.rdap.scraper.Scraper;
import net.apnic.rdap.scraper.ScraperException;
import net.apnic.rdap.scraper.ScraperResult;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

/**
 * A {@link Scraper} implementation that allows to configure multiple delegated stats sources. It may be used, for
 * instance, to load NIR stats.
 */
public class CustomStatsScrapper implements Scraper {

    private final RDAPAuthorityStore rdapAuthorityStore;
    private final List<DelegatedStatsScraper> customScrapers;


    public CustomStatsScrapper(RDAPAuthorityStore rdapAuthorityStore, List<CustomEntry> customEntries) {
        this.rdapAuthorityStore = rdapAuthorityStore;
        this.customScrapers = initialiseCustomScrapers(customEntries);
    }

    private List<DelegatedStatsScraper> initialiseCustomScrapers(List<CustomEntry> customEntries) {
        return customEntries.stream()
                .map(e -> new InternalCustomScrapper(rdapAuthorityStore, e.getUri(), e.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return "custom-scrapper";
    }

    @Override
    public ScraperResult fetchData() throws ScraperException {
        List<ScraperResult> results = new ArrayList<>(customScrapers.size());
        for (Scraper scraper : customScrapers) {
            results.add(scraper.fetchData());
        }

        return mergeScraperResults(results);
    }

    private ScraperResult mergeScraperResults(List<ScraperResult> results) {
        BinaryOperator<ScraperResult> mergeResults = (sr1, sr2) ->
                new ScraperResult(mergeList(sr1.getIpMappings(), sr2.getIpMappings()),
                                  mergeList(sr1.getAsnMappings(), sr2.getAsnMappings()),
                                  mergeList(sr1.getDomainMappings(), sr2.getDomainMappings()));
        return results.stream()
                .reduce(new ScraperResult(Collections.emptyList(), Collections.emptyList(), Collections.emptyList()),
                        mergeResults);
    }

    private <T> List<T> mergeList(Optional<List<T>> maybeL1, Optional<List<T>> maybeL2)  {
        List<T> l1 = maybeL1.orElse(Collections.emptyList());
        List<T> l2 = maybeL2.orElse(Collections.emptyList());
        List<T> result = new ArrayList<>(l1.size() + l2.size());
        result.addAll(l1);
        result.addAll(l2);
        return result;
    }

    private static class InternalCustomScrapper extends DelegatedStatsScraper {

        private String name;

        InternalCustomScrapper(RDAPAuthorityStore rdapAuthorityStore, String statsURI, String name) {
            super(rdapAuthorityStore, URI.create(statsURI));
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
