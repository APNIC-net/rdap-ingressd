package net.apnic.rdap.stats.parser;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import net.apnic.rdap.autnum.AsnRange;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ObjectArrayArguments;
import org.junit.jupiter.params.provider.MethodSource;

public class AsnRecordTest
{
    static Stream<Arguments> asnRecords()
        throws IOException
    {
        CSVFormat format = CSVFormat
                            .DEFAULT
                            .withDelimiter('|');

        return Stream.of(
            ObjectArrayArguments.create(
                CSVParser.parse("apnic|AU|asn|1659|1|20020801|allocated",
                               format)),
            ObjectArrayArguments.create(
                CSVParser.parse("apnic|AU|asn|1659|100|20020801|allocated",
                               format)),
            ObjectArrayArguments.create(
                CSVParser.parse("apnic|AU|asn|30000|1|20020801|allocated",
                               format)),
            ObjectArrayArguments.create(
                CSVParser.parse("apnic|AU|asn|30000|100|20020801|allocated",
                               format)));
    }

    @ParameterizedTest
    @MethodSource(names = "asnRecords")
    void checkFitsAndConstruction(CSVParser parser)
        throws IOException
    {
        List<CSVRecord> records = parser.getRecords();

        for(CSVRecord record : records)
        {
            assertTrue(AsnRecord.fits(record));

            new AsnRecord(record);
        }
    }

    @Test
    void checkToAsnRange()
    {
        AsnRecord record1 = new AsnRecord("apnic", "1024", "1");
        AsnRecord record2 = new AsnRecord("apnic", "1024", "100");
        AsnRange range1 = AsnRange.parse("1024");
        AsnRange range2 = AsnRange.parse("1024-1123");

        assertEquals(record1.toAsnRange(), range1);
        assertEquals(record2.toAsnRange(), range2);
    }
}
