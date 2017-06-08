package net.apnic.rdap.scraper;

import java.util.ArrayList;
import java.util.List;

public class ScraperScheduler
{
    private List<Scraper> scrapers = new ArrayList<Scraper>();

    public void addScraper(Scraper scraper)
    {
        scrapers.add(scraper);
    }

    public void start()
    {
    }
}
