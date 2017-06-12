package net.apnic.rdap.stats.parser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

/**
 * Parsing class for DelegatedStats data input.
 */
public class DelegatedStatsParser
{
    /**
     * Interface for passing handlers for specific resources to the parser.
     */
    public interface RecordHandler<T extends Resource>
    {
        public void handleRecord(T resource);
    }

    // Delimits the start of a comment in a delegated stats file
    public static final char COMMENT_MARKER = '#';

    // Field delimiter
    public static final char DELIMITER = '|';

    /**
     * Main passing method for getting handler callback when valid records are
     * parsed.
     *
     * @param iStream InputStream containing the contents of a delegated stats
     *                file.
     * @param asnHandler Handler callback for dealing with ASN records.
     * @param ipv4Handler callback for dealing with IPv4 records.
     * @param ipv6Handler callback for dealing with IPv6 records.
     * @throws DelegatedStatsException Exception is thrown when ever invalid
     *                                 parsing happens with the delegated stats
     *                                 file.
     */
    public static void parse(InputStream iStream,
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
            throw new DelegatedStatsException(ex);
        }

        Header header = null;
        ParserAnalytics analytics = new ParserAnalytics();

        for(CSVRecord record : records)
        {
            if(Header.fits(record))
            {
                if(header != null)
                {
                    throw new DelegatedStatsException("Header already set");
                }
                header = new Header(record);
            }
            else if(Summary.fits(record))
            {
                analytics.setSummary(new Summary(record));
            }
            else if(AsnRecord.fits(record))
            {
                analytics.incCounterForType(Summary.SummaryType.ASN);
                asnHandler.handleRecord(new AsnRecord(record));
            }
            else if(IPv4Record.fits(record))
            {
                analytics.incCounterForType(Summary.SummaryType.IPV4);
                ipv4Handler.handleRecord(new IPv4Record(record));
            }
            else if(IPv6Record.fits(record))
            {
                analytics.incCounterForType(Summary.SummaryType.IPV6);
                ipv6Handler.handleRecord(new IPv6Record(record));
            }
        }
    }
}
