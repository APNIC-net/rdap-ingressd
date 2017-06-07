package net.apnic.rdap.iana.scraper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.apnic.rdap.scraper.Scraper;

public class IANABootstrapScraper
    implements Scraper
{
    public static final URL ASN_URL;
    public static final URL BASE_URL;
    public static final URL DOMAIN_URL;
    public static final URL IPV4_URL;
    public static final URL IPV6_URL;

    private static final Logger LOGGER =
        Logger.getLogger(IANABootstrapScraper.class.getName());

    static
    {
        URL asnURL = null;
        URL baseURL = null;
        URL domainURL = null;
        URL ipv4URL = null;
        URL ipv6URL = null;

        try
        {
            baseURL = new URL("https://data.iana.org/rdap/");
            asnURL = new URL(baseURL, "/asn.json");
            domainURL = new URL(baseURL, "/dns.json");
            ipv4URL = new URL(baseURL, "/ipv4.json");
            ipv6URL = new URL(baseURL, "/ipv6.json");
        }
        catch(MalformedURLException ex)
        {
            LOGGER.log(Level.SEVERE, "Exception when generating IANA url's",
                       ex);
        }
        finally
        {
            ASN_URL = asnURL;
            BASE_URL = baseURL;
            DOMAIN_URL = domainURL;
            IPV4_URL = ipv4URL;
            IPV6_URL = ipv6URL;
        }
    }
}
