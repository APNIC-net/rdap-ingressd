package net.apnic.rdap.stats.parser;

import org.apache.commons.csv.CSVRecord;

public abstract class Resource
    implements Line
{
    static final int DEFAULT_RECORD_SIZE = 6;

    String registry = null;
    String start = null;
    String type = null;
    String value = null;

    public Resource(CSVRecord record)
    {
        if(fits(record) == false)
        {
            throw new IllegalArgumentException("Not a resource");
        }

        registry = record.get(0);
        type = record.get(2);
        start = record.get(3);
        value = record.get(4);
    }

    public static boolean fits(CSVRecord record)
    {
        return record.size() == DEFAULT_RECORD_SIZE;
    }

    public String getRegistry()
    {
        return registry;
    }

    public String getStart()
    {
        return start;
    }

    public String getType()
    {
        return type;
    }

    public String getValue()
    {
        return value;
    }
}
