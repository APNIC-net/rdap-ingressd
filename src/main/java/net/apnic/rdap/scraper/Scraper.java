package net.apnic.rdap.scraper;

/**
 * Generic interface that scrapers must conform to.
 */
public interface Scraper
{
    /**
     * Name of the scraper used for debuging and error logging purposes.
     */
    String getName();

    /**
     * Triggers the scraper to fetch data.
     * @return a {@link ScraperResult} containing the fetched data
     * @throws ScraperException if an exception occurs during the scraper's execution
     */
    ScraperResult fetchData() throws ScraperException;
}
