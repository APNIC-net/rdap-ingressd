package net.apnic.rdap.domain;

import net.apnic.rdap.domain.*;

import net.ripe.ipresource.IpRange;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

public class DomainTest
{
    @ParameterizedTest
    @ValueSource(strings = {"10.in-addr.arpa", "1.10.in-addr.arpa",
                            "2.1.10.in-addr.arpa", "3.2.1.10.in-addr.arpa"})
    public void domainNameIsArpa4(String arpaDomain)
    {
        Domain domain = new Domain(arpaDomain);
        assertTrue(domain.isArpa());
        assertTrue(domain.isArpa4());
        assertFalse(domain.isArpa6());
    }

    @ParameterizedTest
    @ValueSource(strings = {"2.in6.arpa", "0.2.in6.arpa",
                            "f.0.2.in6.arpa", "f.f.0.2.in6.arpa"})
    public void domainNameIsArpa6(String arpaDomain)
    {
        Domain domain = new Domain(arpaDomain);
        assertTrue(domain.isArpa());
        assertTrue(domain.isArpa6());
        assertFalse(domain.isArpa4());
    }

    @ParameterizedTest
    @CsvSource({"apnic.net, net", "net, net", "test.apnic.net, net",
                "apnic.com.au, au", "apnic.io,io"})
    public void domainNameTLDCheck(String domainName, String tld)
    {
        Domain domain = new Domain(domainName);
        assertEquals(domain.getTLD(), tld);
    }
}
