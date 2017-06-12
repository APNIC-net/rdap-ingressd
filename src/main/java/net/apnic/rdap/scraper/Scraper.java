package net.apnic.rdap.scraper;

import java.util.concurrent.CompletableFuture;

public interface Scraper
{
    public CompletableFuture<Void> start();
}
