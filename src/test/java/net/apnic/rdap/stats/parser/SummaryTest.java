package net.apnic.rdap.stats.parser;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ObjectArrayArguments;
import org.junit.jupiter.params.provider.MethodSource;

public class SummaryTest
{
    static Stream<Arguments> summaryRecords()
        throws IOException
    {
        CSVFormat format = CSVFormat
                            .DEFAULT
                            .withDelimiter('|');

        return Stream.of(
            ObjectArrayArguments.create(
                CSVParser.parse("apnic|*|asn|*|81053|summary", format)),
            ObjectArrayArguments.create(
                CSVParser.parse("apnic|*|ipv4|*|180646|summary", format)),
            ObjectArrayArguments.create(
                CSVParser.parse("apnic|*|ipv6|*|191367|summary", format)));
    }

    @ParameterizedTest
    @MethodSource(names = "summaryRecords")
    void checkHeaderConstruction(CSVParser parser)
        throws IOException
    {
        List<CSVRecord> records = parser.getRecords();

        for(CSVRecord record : records)
        {
            Summary summary = new Summary(record);

            assertEquals(summary.getType(),
                         Summary.SummaryType.getEnum(record.get(2)));
            assertEquals(summary.getNoRecords(),
                         Integer.parseInt(record.get(4)));
        }
    }
}
