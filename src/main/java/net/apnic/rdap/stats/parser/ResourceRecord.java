package net.apnic.rdap.stats.parser;

import org.apache.commons.csv.CSVRecord;

/**
 * Represents a single resource record in a delegated stats file
 */
public abstract class ResourceRecord
    implements Line
{
    static final int MIN_RECORD_SIZE = 7;
    static final int MAX_RECORD_SIZE = 9;

    String registry = null;
    String start = null;
    String type = null;
    String value = null;

    /**
     * Constructor for a new ResourceRecord that isn't being constructed through
     * a CSVRecord.
     *
     * @param registry The registry this resource record belongs to
     * @param type Type string to depict the type of resource this record
     *             contains.
     * @param start Starting value for this type of resource record
     * @param value Value that is matched with start for the given resource
     *              record type
     */
    public ResourceRecord(String registry, String type, String start,
                          String value)
    {
        this.registry = registry;
        this.start = start;
        this.type = type;
        this.value = value;
    }

    /**
     * Constructs a new ResourceRecord from a CSVRecord
     *
     * @param record CSVRecord to construct this object from
     */
    public ResourceRecord(CSVRecord record)
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

    /**
     * Convience method that checks if a supplied CSVRecord fits the mold of a
     * resource record.
     *
     * @param record CSVRecord to check
     */
    public static boolean fits(CSVRecord record)
    {
        return record.size() >= MIN_RECORD_SIZE &&
               record.size() <= MAX_RECORD_SIZE;
    }

    /**
     * Returns the registry value for this record.
     *
     * @return Registry value
     */
    public String getRegistry()
    {
        return registry;
    }

    /**
     * Returns the start value for this record
     *
     * @return Resource record start value
     */
    public String getStart()
    {
        return start;
    }

    /**
     * Type value for this record
     *
     * @return Resource type value
     */
    public String getType()
    {
        return type;
    }

    /**
     * Resource value that is used in conjunction with the start value
     *
     * @return Resource value
     */
    public String getValue()
    {
        return value;
    }
}
