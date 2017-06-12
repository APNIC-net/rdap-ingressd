package net.apnic.rdap.stats.parser;

import net.apnic.rdap.autnum.AsnRange;

import org.apache.commons.csv.CSVRecord;

/**
 * Represents a single autnum record in a delegated stats file.
 */
public class AsnRecord
    extends Resource
{
    public static final String ASN_TYPE = "asn";

    /**
     * Constructs a basic AsnRecord from must have values.
     *
     * @param registry Registry that owns the autnum record
     * @param start Starting autnum value
     * @param value Value specifier for autmum records
     */
    public AsnRecord(String registry, String start, String value)
    {
        super(registry, ASN_TYPE, start, value);
    }

    /**
     * Takes a CSV record that represents a single autnum record for a delegated
     * stats file.
     *
     * @param record CSVRecord that contains the header information
     * @throws IllegalArgumentException If CSVRecord is not an autnum record
     */
    public AsnRecord(CSVRecord record)
    {
        super(record);

        if(fits(record) == false)
        {
            throw new IllegalArgumentException("Not a valid autnum record");
        }
    }

    /**
     * Checks a given CSVRecord to confirm if it's a valid autnum record.
     *
     * @param record CSVRecord to check
     * @return True if record is an autnum record
     */
    public static boolean fits(CSVRecord record)
    {
        return Resource.fits(record) && record.get(2).equals(ASN_TYPE);
    }

    /**
     * Converts this autnum record to an AsnRange object.
     *
     * @return AsnRange representation of record
     */
    public AsnRange toAsnRange()
    {
        int asnStart = Integer.parseInt(getStart());
        int value = Integer.parseInt(getValue());
        int asnEnd = asnStart + --value;

        return AsnRange.parse(String.format("%d%s%d", asnStart,
                                            AsnRange.ASN_RANGE_SEPARATOR,
                                            asnEnd));
    }
}
