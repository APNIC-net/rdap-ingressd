package net.apnic.rdap.stats.parser;

import org.apache.commons.csv.CSVRecord;

public abstract class Resource
    implements Line
{
    static final int MIN_RECORD_SIZE = 7;
    static final int MAX_RECORD_SIZE = 9;

    String registry = null;
    String start = null;
    String type = null;
    String value = null;

    public Resource(String registry, String type, String start, String value)
    {
        this.registry = registry;
        this.start = start;
        this.type = type;
        this.value = value;
    }

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
        return record.size() >= MIN_RECORD_SIZE &&
               record.size() <= MAX_RECORD_SIZE;
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
