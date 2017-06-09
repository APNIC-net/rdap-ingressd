package net.apnic.rdap.stats.parser;

import org.apache.commons.csv.CSVRecord;

public class IPv4Record
    extends Resource
{
    public IPv4Record(CSVRecord record)
    {
        super(record);
    }

    public static boolean fits(CSVRecord record)
    {
        return record.size() == DEFAULT_RECORD_SIZE &&
               record.get(2).equals("ipv4");
    }
}
