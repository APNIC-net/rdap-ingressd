package net.apnic.rdap.iana.scraper;

import java.util.List;

/**
 * Exception is thrown when a bootstrap file version is not supported or
 * expected.
 */
public class BootstrapVersionException
    extends RuntimeException
{
    /**
     * Takes the recieved version from the bootstrap file and a list of
     * supported versions that where expected.
     *
     * @param recievedVersion Version recieved from the bootstrap file
     * @param supportedVersions List of expected/supported versions
     */
    public BootstrapVersionException(String recievedVersion,
                                     List<String> supportedVersions)
    {
        super(String.format("Invalid version \"%s\", supported versions are: " +
                            "\"%s\"", recievedVersion, supportedVersions));
    }
}
