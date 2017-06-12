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

    /**
     * Constructs an exception with the context of another throwable.
     *
     * @param ex Throwable to construct from
     * @see java.lang.Exception
     */
    public DelegatedStatsException(Throwable ex)
    {
        super(ex);
    }
}
