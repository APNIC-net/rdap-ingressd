package net.apnic.rdap.stats.parser;

import org.apache.commons.csv.CSVRecord;

/**
 * Class represents a header record for a delegated stats file
 */
public class Header
    implements Line
{
    private static final int DEFAULT_RECORD_SIZE = 7;

    private String version;

    /**
     * Default constructor.
     *
     * Takes a CSV record that represents the header from a delegated stats
     * file.
     *
     * @param record CSVRecord that contains the header information
     * @throws IllegalArgumentException If CSVRecord is not a header
     */
    public Header(CSVRecord record)
    {
        if(fits(record) == false)
        {
            throw new IllegalArgumentException("Not a header record");
        }

        this.version = record.get(0);
    }

    /**
     * Checks a given CSVRecord to confirm if it's a valid summary record.
     *
     * @param record CSVRecord to check
     * @return True if record is a header
     */
    public static boolean fits(CSVRecord record)
    {
        return record.size() == DEFAULT_RECORD_SIZE;
    }

    /**
     * Returns the version contained in the header.
     *
     * @return version String
     */
    public String getVersion()
    {
        return version;
    }
}
