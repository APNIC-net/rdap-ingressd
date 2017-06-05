package net.apnic.rdap.autnum;

import net.ripe.ipresource.Asn;
import net.ripe.ipresource.IpResourceRange;

public class AsnRange
    extends IpResourceRange
{
    public final static String ASN_RANGE_SEPARATOR = "-";

    protected AsnRange(Asn start, Asn end)
    {
        super(start, end);
    }

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
