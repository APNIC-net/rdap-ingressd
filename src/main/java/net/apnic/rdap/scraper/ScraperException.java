package net.apnic.rdap.scraper;

/**
 * Signals an exception when a {@link Scraper} is processing data.
 */
public class ScraperException extends Exception {
    public ScraperException(String message) {
        super(message);
    }

    public ScraperException(String message, Throwable cause) {
        super(message, cause);
    }
}
