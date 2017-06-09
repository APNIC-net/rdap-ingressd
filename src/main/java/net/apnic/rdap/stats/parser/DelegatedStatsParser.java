package net.apnic.rdap.stats.parser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class DelegatedStatsParser
{
    public interface RecordHandler<T extends Resource>
    {
        public void handleRecord(T resource);
    }

    public static final char COMMENT_MARKER = '#';
    public static final char DELIMITER = '|';

    public void parse(InputStream iStream,
                      RecordHandler<AsnRecord> asnHandler,
                      RecordHandler<IPv4Record> ipv4Handler,
                      RecordHandler<IPv6Record> ipv6Handler)
        throws DelegatedStatsException
    {
        Iterable<CSVRecord> records = null;
        try
        {
            records = CSVFormat
                    .DEFAULT
                    .withCommentMarker(COMMENT_MARKER)
                    .withDelimiter(DELIMITER)
                    .parse(new InputStreamReader(iStream));
        }
        catch(IOException ex)
        {
            throw new RuntimeException(ex);
        }

        Header header = null;
        boolean headerSet = false;

        for(CSVRecord record : records)
        {
            if(Header.fits(record))
            {
                if(headerSet)
                {
                    throw new DelegatedStatsException("Header already set");
                }
                headerSet = true;
                header = new Header(record);
            }
            else if(Summary.fits(record))
            {
            }
            else if(AsnRecord.fits(record))
            {
                asnHandler.handleRecord(new AsnRecord(record));
            }
            else if(IPv4Record.fits(record))
            {
                ipv4Handler.handleRecord(new IPv4Record(record));
            }
            else if(IPv6Record.fits(record))
            {
                ipv6Handler.handleRecord(new IPv6Record(record));
            }
        }
    }
}
