package net.apnic.rdap.iana.scraper;

import java.util.List;

public class BootstrapVersionException
    extends RuntimeException
{
    public BootstrapVersionException(String recievedVersion,
                                     List<String> supportedVersions)
    {
        super(String.format("Invalid version \"%s\", supported versions are: " +
                            "\"%s\"", recievedVersion, supportedVersions));
    }
}
