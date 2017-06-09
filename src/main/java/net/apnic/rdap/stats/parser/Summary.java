package net.apnic.rdap.stats.parser;

import org.apache.commons.csv.CSVRecord;

public class Summary
    implements Line
{
    private static final int DEFAULT_RECORD_SIZE = 6;

    private String type = null;

    public Summary(CSVRecord record)
    {
        this.type = record.get(2);
    }

    public static boolean fits(CSVRecord record)
    {
        return record.size() == DEFAULT_RECORD_SIZE &&
               record.get(5).equals("summary");
    }
}
