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

public class HeaderTest
{
    static Stream<Arguments> headerRecords()
        throws IOException
    {
        CSVFormat format = CSVFormat
                            .DEFAULT
                            .withDelimiter('|');

        return Stream.of(
            ObjectArrayArguments.create(
                CSVParser.parse("2|nro|20170608|451917|19821213|20170608|+0000",
                                format)),
            ObjectArrayArguments.create(
                CSVParser.parse("2.1|nro|20170608|451917|19821213|20170608|+0000",
                                format)),
            ObjectArrayArguments.create(
                CSVParser.parse("2.2|nro|20170608|451917|19821213|20170608|+0000",
                                format)));
    }

    @ParameterizedTest
    @MethodSource(names = "headerRecords")
    void checkHeaderConstruction(CSVParser parser)
        throws IOException
    {
        List<CSVRecord> records = parser.getRecords();

        for(CSVRecord record : records)
        {
            Header header = new Header(record);

            assertEquals(header.getVersion(), record.get(0));
        }
    }
}
