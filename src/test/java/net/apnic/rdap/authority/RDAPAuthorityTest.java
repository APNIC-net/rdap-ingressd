package net.apnic.rdap.authority;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ObjectArrayArguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class RDAPAuthorityTest
{
    static Stream<Arguments> validAuthorityParams()
        throws URISyntaxException
    {
        return Stream.of(
            ObjectArrayArguments.create(
                "apnic", Arrays.asList("apnic1", "apnic2", "apnic3"),
                Arrays.asList(new URI("https://rdap.apnic.net/"),
                              new URI("http://rdap.apnic.net/"))),
            ObjectArrayArguments.create(
                "ripe", Arrays.asList("ripencc", "ripe1", "ripe2"),
                Arrays.asList(new URI("https://rdap.db.ripe.net/"))),
            ObjectArrayArguments.create(
                "arin", Arrays.asList("arin1", "arin2", "arin3"),
                Arrays.asList(new URI("https://rdap.arin.net/registry/"))));
    }

    @ParameterizedTest
    @ValueSource(strings = {"apnic", "arin", "lacnic", "ripe", "afrinic"})
    void checkRDAPAuthorityNameSetting(String authorityName)
    {
        RDAPAuthority authority = new RDAPAuthority(authorityName);
        assertEquals(authority.getName(), authorityName);
    }

    @Test
    void checkInvalidRDAPAuthorityNameSetting()
    {
        assertThrows(IllegalArgumentException.class, () ->
        {
            RDAPAuthority authority = new RDAPAuthority(null);
        });

        assertThrows(IllegalArgumentException.class, () ->
        {
            RDAPAuthority authority = new RDAPAuthority("      ");
        });
    }

    @ParameterizedTest
    @MethodSource(names = "validAuthorityParams")
    void checkAliasAdding(String name, List<String> aliases, List<URI> servers)
    {
        RDAPAuthority authority = new RDAPAuthority(name);
        authority.setAliases(aliases);

        assertEquals(authority.getAliases(), aliases);

        authority = new RDAPAuthority(name);

        authority.setAliases(aliases);

        assertEquals(authority.getAliases(), aliases);
    }

    @Test
    void checkInvalidAliasAdding()
    {
        List<String> aliases = Arrays.asList(null, "        ");
        RDAPAuthority authority = new RDAPAuthority("apnic");

        assertThrows(IllegalArgumentException.class, () ->
        {
            authority.setAliases(aliases);
        });
    }

    @ParameterizedTest
    @MethodSource(names = "validAuthorityParams")
    void checkServerAdding(String name, List<String> aliases, List<URI> servers)
    {
        RDAPAuthority authority = new RDAPAuthority(name);
        authority.setIanaBootstrapRefServers(servers);

        assertEquals(authority.getIanaBootstrapRefServers(), servers);
    }

    @Test
    void checkInvalidServerAdding()
    {
        RDAPAuthority authority = new RDAPAuthority("apnic");

        assertThrows(IllegalArgumentException.class, () ->
        {
            authority.setIanaBootstrapRefServers(null);
        });
    }

    @ParameterizedTest
    @MethodSource(names = "validAuthorityParams")
    void checkEquals(String name, List<String> aliases, List<URI> servers)
    {
        RDAPAuthority authority1 = new RDAPAuthority(name);
        RDAPAuthority authority2 = new RDAPAuthority(name);
        RDAPAuthority authority3 = new RDAPAuthority("doesnotexist");

        authority1.setAliases(aliases);
        authority1.setIanaBootstrapRefServers(servers);
        authority2.setAliases(aliases);
        authority2.setIanaBootstrapRefServers(servers);

        assertEquals(authority1, authority1);
        assertNotEquals(authority1, null);
        assertNotEquals(authority1, new Object());
        assertEquals(authority1, authority2);
        assertNotEquals(authority1, authority3);
    }
}
