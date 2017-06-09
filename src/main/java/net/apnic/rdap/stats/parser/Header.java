package net.apnic.rdap.stats.parser;

import org.apache.commons.csv.CSVRecord;

public class Header
    implements Line
{
    private static final int DEFAULT_RECORD_SIZE = 7;

    private String version;

    public Header(CSVRecord record)
    {
        this.version = record.get(0);
    }

    public static boolean fits(CSVRecord record)
    {
        return record.size() == DEFAULT_RECORD_SIZE;
    }

    public String getVersion()
    {
        return version;
    }
}
