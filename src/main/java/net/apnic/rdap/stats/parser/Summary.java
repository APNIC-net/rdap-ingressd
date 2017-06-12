package net.apnic.rdap.stats.parser;

import org.apache.commons.csv.CSVRecord;

/**
 * Class represents a summary record for a delegated stats file
 */
public class Summary
    implements Line
{
    public enum SummaryType
    {
        ASN("asn"),
        IPV4("ipv4"),
        IPV6("ipv6");

        private final String typeStr;

        private SummaryType(String typeStr)
        {
            this.typeStr = typeStr;
        }

        public String toString()
        {
            return this.typeStr;
        }
    }

    private static final int DEFAULT_RECORD_SIZE = 6;

    private int noRecords = 0;
    private SummaryType type = null;
    /**
     * Constructs a new summary with the supplied type and expected number of
     * records count.
     *
     * @param type SummaryType of object
     * @param noRecord Number of records for this type to expect.
     */
    public Summary(SummaryType type, int noRecords)
    {
        this.noRecords = noRecords;
        this.type = type;
    }

    /**
     * Constructs a new Summary object from a CSVRecord.
     *
     * Takes a CSV record that represents the summary line for a delegated stats
     * file.
     *
     * @param record CSVRecord that contains the summary information
     * @throws IllegalArgumentException If CSVRecord is not a summary line.
     */
    public Summary(CSVRecord record)
    {
        if(fits(record) == false)
        {
            throw new IllegalArgumentException("Not a summary record");
        }

        this.noRecords = Integer.parseInt(record.get(5));
        this.type = SummaryType.valueOf(record.get(2));
    }

    /**
     * Checks a given CSVRecord to confirm if it's a valid summary record.
     *
     * @param record CSVRecord record to check
     * @return True if record is a summary line
     */
    public static boolean fits(CSVRecord record)
    {
        return record.size() == DEFAULT_RECORD_SIZE &&
               record.get(5).equals("summary");
    }

    /**
     * Returns the number of records this summary line represents.
     *
     * @return Number of records
     */
    public int getNoRecords()
    {
        return noRecords;
    }

    /**
     * Returns the type of this summary.
     *
     * @return SummaryType
     */
    public SummaryType getType()
    {
        return type;
    }

    /**
     * Returns the type string for the summary record.
     *
     * @return type String
     */
    public String getTypeString()
    {
        return type.toString();
    }
}
