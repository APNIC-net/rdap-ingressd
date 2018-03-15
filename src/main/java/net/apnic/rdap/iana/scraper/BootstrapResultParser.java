package net.apnic.rdap.iana.scraper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

public class BootstrapResultParser
{
    public static final List<String> SUPPORTED_VERSIONS = Arrays.asList("1.0");

    private BootstrapResultParser() {}

    public static BootstrapResult parse(JsonNode bootstrapRawResult) {
        JsonNode version = bootstrapRawResult.get("version");
        if(version == null || SUPPORTED_VERSIONS.contains(version.asText()) == false) {
            throw new BootstrapVersionException(
                version == null ? "null" : version.asText(), SUPPORTED_VERSIONS);
        }

        ObjectMapper oMapper = new ObjectMapper();
        BootstrapResult result = null;

        try {
            result = oMapper.treeToValue(bootstrapRawResult, BootstrapResult.class);
        } catch(JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }

        return result;
    }
}
