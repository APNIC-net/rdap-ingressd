package net.apnic.rdap.stats.parser;

import java.util.HashMap;

/**
 * Class keeps basic counters for the different types of records encounted in
 * a delegated stats file.
 *
 * Used for assessing the validity of a full parse.
 */
class ParserAnalytics
{
    /**
     * Inner class for keeping individual summary record analytics
     */
    private class SummaryAnalytics
    {
        private Summary summary = null;
        private int counter = 0;

        /**
         * Constructs a SummaryAnalytics object with the provided Summary
         * object.
         *
         * Counter is initialised to zero.
         *
         * @param summary Summary object to count against.
         */
        public SummaryAnalytics(Summary summary)
        {
            if(summary == null)
            {
                throw new IllegalArgumentException("summary cannot be null");
            }
            this.summary = summary;
        }

        /**
         * Increments this object's counter against the Summary.
         *
         * If the counter goes higher than the attached Summary's
         * getNoRecords(), a DelegatedStatsException is thrown.
         *
         * @throws DelegatedStatsException When counter is incremented past the
         *                                 summary.
         */
        public void incCounter()
            throws DelegatedStatsException
        {
            ++counter;
            if(counter > summary.getNoRecords())
            {
                throw new DelegatedStatsException(
                    "To many records of type " + summary.getType() +
                    " - " + counter);
            }
        }

        /**
         * Returns the attached summary.
         *
         * @return Attached summary
         */
        public Summary getSummary()
        {
            return summary;
        }
    }

    private HashMap<Summary.SummaryType, SummaryAnalytics> summaries;

    /**
     * Default constructor
     */
    public ParserAnalytics()
    {
        summaries = new HashMap<Summary.SummaryType, SummaryAnalytics>();
    }

    /**
     * Increments the analytics counter for the provided summary type.
     *
     * If the provided summary type has not been set then a
     * DelegatedStatsException is thrown.
     *
     * If the increment counter for the given summary type goes over then a
     * DelegatedStatsException is thrown.
     *
     * @param type Type of summary to increment for
     * @throw DelegatedStatsException
     */
    public void incCounterForType(Summary.SummaryType type)
        throws DelegatedStatsException
    {
        SummaryAnalytics analytics = summaries.get(type);

        if(analytics == null)
        {
            throw new DelegatedStatsException("No summary analytics for " +
                                              type);
        }

        analytics.incCounter();
    }

    /**
     * Adds the provided summary into the analytics.
     *
     * Throws a new DelegatedStatsException if the given summary type already
     * exists.
     *
     * @param summary Summary to add
     * @throws DelegatedStatsException When given summary type alread exists.
     */
    public void setSummary(Summary summary)
        throws DelegatedStatsException
    {
        if(summaries.containsKey(summary.getType()))
        {
            throw new DelegatedStatsException("Summary for " + summary.getType()
                                              + " already exists");
        }

        summaries.put(summary.getType(), new SummaryAnalytics(summary));
    }
}
