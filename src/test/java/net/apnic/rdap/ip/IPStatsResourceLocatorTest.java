package net.apnic.rdap.ip;

import java.util.stream.Stream;

import net.apnic.rdap.authority.RDAPAuthority;
import net.apnic.rdap.resource.ResourceNotFoundException;

import net.ripe.ipresource.IpRange;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ObjectArrayArguments;
import org.junit.jupiter.params.provider.MethodSource;

public class IPStatsResourceLocatorTest
{
    static Stream<Arguments> exactMatchingParams()
    {
        RDAPAuthority authority = new RDAPAuthority("apnic");

        return Stream.of(
            ObjectArrayArguments.create(
                IpRange.parse("10.0.0.0/8"), authority),
            ObjectArrayArguments.create(
                IpRange.parse("10.1.0.0/16"), authority),
            ObjectArrayArguments.create(
                IpRange.parse("10.1.1.0/24"), authority),
            ObjectArrayArguments.create(
                IpRange.parse("10.1.1.1/32"), authority),
            ObjectArrayArguments.create(
                IpRange.parse("fe80::/10"), authority),
            ObjectArrayArguments.create(
                IpRange.parse("fe80:2001::/32"), authority),
            ObjectArrayArguments.create(
                IpRange.parse("fe80:2001:1:1::/64"), authority),
            ObjectArrayArguments.create(
                IpRange.parse("fe80:2001:1:1::1/128"), authority));
    }

    @Test
    void checkNoExistResourceLookup()
    {
        IPStatsResourceLocator resourceLocator = new IPStatsResourceLocator();

        assertThrows(ResourceNotFoundException.class, () ->
        {
            IpRange range = IpRange.parse("10.16.0.0/16");
            resourceLocator.authorityForResource(range);
        });
    }

    @ParameterizedTest
    @MethodSource(names = "exactMatchingParams")
    void checkExactMatching(IpRange range, RDAPAuthority authority)
        throws ResourceNotFoundException
    {
        IPStatsResourceLocator resourceLocator = new IPStatsResourceLocator();
        resourceLocator.putResourceMapping(range, authority);

        assertEquals(resourceLocator.authorityForResource(range), authority);
    }

    @Test
    void checkIp4TreeMatching()
        throws ResourceNotFoundException
    {
        IPStatsResourceLocator resourceLocator = new IPStatsResourceLocator();
        RDAPAuthority authority1 = new RDAPAuthority("apnic1");
        RDAPAuthority authority2 = new RDAPAuthority("apnic2");
        RDAPAuthority authority3 = new RDAPAuthority("apnic3");
        RDAPAuthority authority4 = new RDAPAuthority("apnic4");

        resourceLocator.putResourceMapping(IpRange.parse("10.0.0.0/8"),
                                           authority1);
        resourceLocator.putResourceMapping(IpRange.parse("10.1.0.0/16"),
                                           authority2);
        resourceLocator.putResourceMapping(IpRange.parse("10.1.1.0/24"),
                                           authority3);
        resourceLocator.putResourceMapping(IpRange.parse("10.1.1.218/32"),
                                           authority4);

        assertEquals(resourceLocator.authorityForResource(
                        IpRange.parse("10.0.12.0/24")), authority1);
        assertEquals(resourceLocator.authorityForResource(
                        IpRange.parse("10.1.12.0/24")), authority2);
        assertEquals(resourceLocator.authorityForResource(
                        IpRange.parse("10.1.0.1/32")), authority2);
        assertEquals(resourceLocator.authorityForResource(
                        IpRange.parse("10.1.1.1/32")), authority3);
        assertEquals(resourceLocator.authorityForResource(
                        IpRange.parse("10.1.1.220/32")), authority3);
        assertEquals(resourceLocator.authorityForResource(
                        IpRange.parse("10.1.1.218/32")), authority4);
    }

    @Test
    void checkIp6TreeMatching()
        throws ResourceNotFoundException
    {
        IPStatsResourceLocator resourceLocator = new IPStatsResourceLocator();
        RDAPAuthority authority1 = new RDAPAuthority("apnic1");
        RDAPAuthority authority2 = new RDAPAuthority("apnic2");
        RDAPAuthority authority3 = new RDAPAuthority("apnic3");
        RDAPAuthority authority4 = new RDAPAuthority("apnic4");

        resourceLocator.putResourceMapping(IpRange.parse("fe80::/10"),
                                           authority1);
        resourceLocator.putResourceMapping(IpRange.parse("fe80:2001::/32"),
                                           authority2);
        resourceLocator.putResourceMapping(IpRange.parse("fe80:2001::/64"),
                                           authority3);
        resourceLocator.putResourceMapping(IpRange.parse("fe80:2001::1/128"),
                                           authority4);

        assertEquals(resourceLocator.authorityForResource(
                        IpRange.parse("fe80:1000::/24")), authority1);
        assertEquals(resourceLocator.authorityForResource(
                        IpRange.parse("fe80:2002::/32")), authority1);
        assertEquals(resourceLocator.authorityForResource(
                        IpRange.parse("fe80:2001:1::/48")), authority2);
        assertEquals(resourceLocator.authorityForResource(
                        IpRange.parse("fe80:2001:1::/64")), authority2);
        assertEquals(resourceLocator.authorityForResource(
                        IpRange.parse("fe80:2001:0:0:a::/80")), authority3);
        assertEquals(resourceLocator.authorityForResource(
                        IpRange.parse("fe80:2001::a/128")), authority3);
        assertEquals(resourceLocator.authorityForResource(
                        IpRange.parse("fe80:2001::1/128")), authority4);
        /*assertEquals(resourceLocator.authorityForResource(
                        IpRange.parse("10.1.12.0/24")), authority2);
        assertEquals(resourceLocator.authorityForResource(
                        IpRange.parse("10.1.0.1/32")), authority2);
        assertEquals(resourceLocator.authorityForResource(
                        IpRange.parse("10.1.1.1/32")), authority3);
        assertEquals(resourceLocator.authorityForResource(
                        IpRange.parse("10.1.1.220/32")), authority3);
        assertEquals(resourceLocator.authorityForResource(
                        IpRange.parse("10.1.1.218/32")), authority4);*/
    }
}
