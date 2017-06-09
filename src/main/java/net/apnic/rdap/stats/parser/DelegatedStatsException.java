package net.apnic.rdap.stats.parser;

/**
 * Generic exception class that represents errors during delegated stats
 * passing.
 */
public class DelegatedStatsException
    extends Exception
{
    /**
     * Default constructor
     *
     * @param message Exception memssage
     * @see java.lang.Exception
     */
    public DelegatedStatsException(String message)
    {
        super(message);
    }
}
