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

    public AsnRecord(String registry, String start, String value)
    {
        super(registry, ASN_TYPE, start, value);
    }

    public AsnRecord(CSVRecord record)
    {
        super(record);
    }

    public static boolean fits(CSVRecord record)
    {
        return Resource.fits(record) && record.get(2).equals(ASN_TYPE);
    }

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
