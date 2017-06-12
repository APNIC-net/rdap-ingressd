package net.apnic.rdap.stats.parser;

import org.apache.commons.csv.CSVRecord;

/**
 * Represents a single ipv4 record in a delegated stats file.
 */
public class IPv4Record
    extends Resource
{
    public static final String IPV4_TYPE = "ipv4";

    /**
     * Takes a CSV record that represents a single ipv4 record for a delegated
     * stats file.
     *
     * @param record CSVRecord that contains the ipv4 information
     * @throws IllegalArgumentException If CSVRecord is not an ipv4 record
     */
    public IPv4Record(CSVRecord record)
    {
        super(record);

        if(fits(record) == false)
        {
            throw new IllegalArgumentException("Not a valid ipv4 record");
        }
    }

    /**
     * Checks a given CSVRecord to confirm if it's a valid ipv4 record.
     *
     * @param record CSVRecord to check
     * @return True if record is ipv4 record
     */
    public static boolean fits(CSVRecord record)
    {
        return Resource.fits(record) && record.get(2).equals(IPV4_TYPE);
    }
}
