package net.apnic.rdap.stats.scraper;

/**
 * Encapsulates data for a custom scrape entry in the configuration file.
 */
public class CustomEntry {
    private String name;
    private String uri;

   public String getName() {
       return name;
   }

   public void setName(String name) {
       this.name = name;
   }

   public String getUri() {
       return uri;
   }

   public void setUri(String uri) {
       this.uri = uri;
   }
}
