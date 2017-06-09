
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
        return record.size() == DEFAULT_RECORD_SIZE &&
               record.get(2).equals("ipv6");
    }
}
