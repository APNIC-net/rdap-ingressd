
package net.apnic.rdap.stats.parser;

import org.apache.commons.csv.CSVRecord;

public class IPv6Record
    extends Resource
{
    public IPv6Record(CSVRecord record)
    {
        super(record);
    }

    public static boolean fits(CSVRecord record)
    {
        return Resource.fits(record) && record.get(2).equals("ipv6");
    }
}
