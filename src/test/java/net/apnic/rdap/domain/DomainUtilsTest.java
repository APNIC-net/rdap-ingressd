package net.apnic.rdap.domain;

import java.util.stream.Stream;

import net.apnic.rdap.domain.*;

import net.ripe.ipresource.IpRange;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ObjectArrayArguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigInteger;

public class DomainUtilsTest
{
    static Stream<Arguments> validArpa4Provider()
    {
        return Stream.of(
            ObjectArrayArguments.create(new Domain("10.in-addr.arpa"),
                                        IpRange.parse("10.0.0.0/8")),
            ObjectArrayArguments.create(new Domain("3.10.in-addr.arpa"),
                                        IpRange.parse("10.3.0.0/16")),
            ObjectArrayArguments.create(new Domain("2.3.10.in-addr.arpa"),
                                        IpRange.parse("10.3.2.0/24")),
            ObjectArrayArguments.create(new Domain("1.2.3.10.in-addr.arpa"),
                                        IpRange.parse("10.3.2.1/32")),
            ObjectArrayArguments.create(new Domain("243.222.192.160.in-addr.arpa"),
                                        IpRange.parse("160.192.222.243/32")));
    }

    static Stream<Arguments> validArpa6Provider()
    {
        return Stream.of(
            ObjectArrayArguments.create(new Domain("a.ip6.arpa"),
                                        IpRange.parse("a000::/4")),
            ObjectArrayArguments.create(new Domain("a.0.ip6.arpa"),
                                        IpRange.parse("a00::/8")),
            ObjectArrayArguments.create(new Domain("1.0.0.2.ip6.arpa"),
                                        IpRange.parse("2001::/16")),
            ObjectArrayArguments.create(new Domain("f.1.0.0.2.ip6.arpa"),
                                        IpRange.parse("2001:f000::/20")),
            ObjectArrayArguments.create(
                new Domain("d.0.0.0.c.0.0.0.b.0.0.0.a.0.0.0.ip6.arpa"),
                IpRange.parse("a:b:c:d::/64")),
            ObjectArrayArguments.create(new Domain("0.8.e.f.ip6.arpa"),
                                        IpRange.parse("fe80::/16")));
    }

    @ParameterizedTest
    @MethodSource(names = "validArpa4Provider")
    void checkArpa4DomainToIPParsing(Domain arpaDomain, IpRange ipAddress)
    {
        IpRange parsedAddress = DomainUtils.ipAddressForArpaDomain(arpaDomain);
        assertEquals(parsedAddress, ipAddress);
    }

    @ParameterizedTest
    @ValueSource(strings = {".in-addr.arpa", "in-addr.arpa", "f.in-addr.arpa",
                            "f.c.a.in-addr.arpa", "1.1.1.1.1.1.1.in-addr.arpa",
                            "10.1.2.3.4.in-addr.arpa", "-10.in-addr.arpa",
                            "-243.in-addr.arpa", "256.in-addr.arpa"})
    void checkInvalidArpa4DomainToIPParsing(String arpaDomain)
    {
        Domain domain = new Domain(arpaDomain);
        assertThrows(IllegalArgumentException.class, () ->
        {
            DomainUtils.ipAddressForArpaDomain(domain);
        });
    }

    @ParameterizedTest
    @MethodSource(names = "validArpa6Provider")
    void checkArpa6DomainToIPParsing(Domain arpaDomain, IpRange ipAddress)
    {
        IpRange parsedAddress = DomainUtils.ipAddressForArpaDomain(arpaDomain);
        assertEquals(parsedAddress, ipAddress);
    }

    @ParameterizedTest
    @ValueSource(strings = {".ip6.arpa", "ip6.arpa", "w.ip6.arpa",
                            "w.a.a.ip6.arpa", "a.f4.ip6.arpa", "a.-f.ip6.arpa",
                            "1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.1.ip6.arpa"})
    void checkInvalidArap6DomainToIPParsing(String arpaDomain)
    {
        Domain domain = new Domain(arpaDomain);
        assertThrows(IllegalArgumentException.class, () ->
        {
            DomainUtils.ipAddressForArpaDomain(domain);
        });
    }
}
