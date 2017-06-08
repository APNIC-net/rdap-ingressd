package net.apnic.rdap.scraper;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.List;

public class ScraperScheduler
{
    private ScheduledExecutorService executor = null;
    private List<Scraper> scrapers = new ArrayList<Scraper>();
    private boolean started = false;

    public ScraperScheduler()
    {
        executor = Executors.newScheduledThreadPool(1);
    }

    public void addScraper(Scraper scraper)
    {
        scrapers.add(scraper);
    }

    public void start()
    {
        if(started == true)
        {
            return;
        }
        started = true;

        for(Scraper scraper : scrapers)
        {
            executor.scheduleAtFixedRate(scraper::start, 0, 12, TimeUnit.HOURS);
        }
    }
}
