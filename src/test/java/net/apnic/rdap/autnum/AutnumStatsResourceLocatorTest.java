package net.apnic.rdap.autnum;

import java.util.stream.Stream;

import net.apnic.rdap.authority.RDAPAuthority;
import net.apnic.rdap.resource.ResourceNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ObjectArrayArguments;
import org.junit.jupiter.params.provider.MethodSource;

public class AutnumStatsResourceLocatorTest
{
    static Stream<Arguments> exactMatchingParams()
    {
        RDAPAuthority authority = new RDAPAuthority("apnic");

        return Stream.of(
            ObjectArrayArguments.create(
                AsnRange.parse("1234"), authority),
            ObjectArrayArguments.create(
                AsnRange.parse("20000"), authority),
            ObjectArrayArguments.create(
                AsnRange.parse("20000-20005"), authority));
    }

    @Test
    void checkNoExistResourceLookup()
    {
        AutnumStatsResourceLocator resourceLocator =
            new AutnumStatsResourceLocator();

        assertThrows(ResourceNotFoundException.class, () ->
        {
            AsnRange range = AsnRange.parse("1001");
            resourceLocator.authorityForResource(range);
        });
    }

    @ParameterizedTest
    @MethodSource(names = "exactMatchingParams")
    void checkExactMatching(AsnRange range, RDAPAuthority authority)
        throws ResourceNotFoundException
    {
        AutnumStatsResourceLocator resourceLocator =
            new AutnumStatsResourceLocator();
        resourceLocator.putResourceMapping(range, authority);

        assertEquals(resourceLocator.authorityForResource(range), authority);
    }

    @Test
    void checkTreeMatching()
        throws ResourceNotFoundException
    {
        AutnumStatsResourceLocator resourceLocator =
            new AutnumStatsResourceLocator();
        RDAPAuthority authority1 = new RDAPAuthority("apnic1");
        RDAPAuthority authority2 = new RDAPAuthority("apnic2");
        RDAPAuthority authority3 = new RDAPAuthority("apnic3");

        resourceLocator.putResourceMapping(AsnRange.parse("1024-2048"),
                                           authority1);
        resourceLocator.putResourceMapping(AsnRange.parse("1030-1035"),
                                           authority2);
        resourceLocator.putResourceMapping(AsnRange.parse("1031"),
                                           authority3);

        assertEquals(resourceLocator.authorityForResource(
                        AsnRange.parse("1050")), authority1);
        assertEquals(resourceLocator.authorityForResource(
                        AsnRange.parse("1032")), authority2);
        assertEquals(resourceLocator.authorityForResource(
                        AsnRange.parse("1031")), authority3);
    }
}
