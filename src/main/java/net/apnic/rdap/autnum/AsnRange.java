package net.apnic.rdap.autnum;

import net.ripe.ipresource.Asn;
import net.ripe.ipresource.IpResourceRange;

/**
 * Provides a concrete RIPE IP Resource library implementation for autnum
 * ranges.
 */
public class AsnRange
    extends IpResourceRange
{
    public final static String ASN_RANGE_SEPARATOR = "-";

    /**
     * Protected constructor. Parse should be used by public users.
     */
    protected AsnRange(Asn start, Asn end)
    {
        super(start, end);
    }

    public boolean isContiguousWith(AsnRange asnRange)
    {
        return getEnd().successor().equals(asnRange.getStart());
    }

    public AsnRange makeContiguousWith(AsnRange asnRange)
    {
        if(isContiguousWith(asnRange) == false)
            throw new RuntimeException("AsnRange's are not alligned");
        return new AsnRange((Asn)getStart(), (Asn)asnRange.getEnd());
    }

    /**
     * Passes an autnum range or a single autnum
     * 
     * Examples are 1234-1239 or 1234
     *
     * @param asnRangeStr The autnum range string to parse
     * @return AsnRange specificying the contents of the parsed string
     * @throws IllegalArgumentException
     */
    public static AsnRange parse(String asnRangeStr)
        throws IllegalArgumentException
    {
        if(asnRangeStr.indexOf(ASN_RANGE_SEPARATOR) == -1)
        {
            asnRangeStr += ASN_RANGE_SEPARATOR + asnRangeStr;
        }

        IpResourceRange asnRange = IpResourceRange.parse(asnRangeStr);
        if(asnRange.getStart() instanceof Asn == false ||
           asnRange.getEnd() instanceof Asn == false)
        {
            throw new IllegalArgumentException(
                "Was not a valid ASN range");
        }

        return new AsnRange((Asn)asnRange.getStart(), (Asn)asnRange.getEnd());
    }
}
