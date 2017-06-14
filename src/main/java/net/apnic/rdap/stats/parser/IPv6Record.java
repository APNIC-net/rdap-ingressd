package net.apnic.rdap.stats.parser;

import net.ripe.ipresource.IpRange;

import org.apache.commons.csv.CSVRecord;

/**
 * Represents a single ipv6 record in a delegated stats file.
 */
public class IPv6Record
    extends IPRecord
{
    /**
     * Takes a CSV record that represents a single ipv6 record for a delegated
     * stats file.
     *
     * @param record CSVRecord that contains the ipv6 information
     * @throws IllegalArgumentException If CSVRecord is not an ipv6 record
     */
    public IPv6Record(CSVRecord record)
    {
        super(record);

        if(fits(record) == false)
        {
            throw new IllegalArgumentException("Not a valid ipv6 record");
        }
    }

    /**
     * Checks a given CSVRecord to confirm if it's a valid ipv6 record.
     *
     * @param record CSVRecord to check
     * @return True of record is ipv6 record
     */
    public static boolean fits(CSVRecord record)
    {
        return ResourceRecord.fits(record) && record.get(2).equals("ipv6");
    }

    /**
     * {@inheritDocs}
     */
    @Override
    public IpRange toIPRange()
    {
        return IpRange.parse(getStart() + "/" + getValue());
    }
}
