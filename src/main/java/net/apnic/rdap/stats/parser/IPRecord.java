package net.apnic.rdap.stats.parser;

import net.ripe.ipresource.IpRange;

import org.apache.commons.csv.CSVRecord;

/**
 * Abstract class that represents IPRecords in a delegated stats file.
 */
public abstract class IPRecord
    extends ResourceRecord
{
    /**
     * Takes a CSV record that represents a single ipv4 or 6 record.
     *
     * @param record CSVRecord that contains ipv4 or 6 information
     * @throws IllegalArgumentException If CSVRecord is not an ipv4 or 6 record
     */
    public IPRecord(CSVRecord record)
    {
        super(record);
    }

    /**
     * Converts the information contained in this record to an IpRange.
     *
     * @return IpRange covering the information contained in this record
     */
    public abstract IpRange toIPRange();
}
