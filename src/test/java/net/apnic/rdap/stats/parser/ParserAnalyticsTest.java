package net.apnic.rdap.stats.parser;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class ParserAnalyticsTest
{
    @Test
    public void checkSummarySetting()
        throws DelegatedStatsException
    {
        ParserAnalytics analytics = new ParserAnalytics();
        Summary summary = new Summary(Summary.SummaryType.ASN, 1024);

        analytics.setSummary(summary);
    }

    @Test
    public void checkSummaryDoubleSetting()
        throws DelegatedStatsException
    {
        ParserAnalytics analytics = new ParserAnalytics();
        Summary summary = new Summary(Summary.SummaryType.ASN, 1024);

        analytics.setSummary(summary);

        assertThrows(DelegatedStatsException.class, () ->
        {
            analytics.setSummary(summary);
        });
    }

    @Test
    public void checkSummaryCounterIncrementing()
        throws DelegatedStatsException
    {
        ParserAnalytics analytics = new ParserAnalytics();
        Summary summary = new Summary(Summary.SummaryType.ASN, 1024);

        analytics.setSummary(summary);

        for(int i = 0; i < 1024; ++i)
        {
            analytics.incCounterForType(Summary.SummaryType.ASN);
        }
    }

    @Test
    public void checkSummaryCounterOverIncrement()
        throws DelegatedStatsException
    {
        ParserAnalytics analytics = new ParserAnalytics();
        Summary summary = new Summary(Summary.SummaryType.ASN, 1024);

        analytics.setSummary(summary);

        for(int i = 0; i < 1024; ++i)
        {
            analytics.incCounterForType(Summary.SummaryType.ASN);
        }

        assertThrows(DelegatedStatsException.class, () ->
        {
            analytics.incCounterForType(Summary.SummaryType.ASN);
        });
    }

    @Test
    public void checkNonSummaryIncrement()
        throws DelegatedStatsException
    {
        ParserAnalytics analytics = new ParserAnalytics();

        assertThrows(DelegatedStatsException.class, () ->
        {
            analytics.incCounterForType(Summary.SummaryType.ASN);
        });
    }
}
