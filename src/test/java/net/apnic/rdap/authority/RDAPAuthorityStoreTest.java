package net.apnic.rdap.authority;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

public class RDAPAuthorityStoreTest
{
    @Test
    void checkAddingAuthority()
    {
        RDAPAuthority authority = new RDAPAuthority("apnic");
        RDAPAuthorityStore store = new RDAPAuthorityStore();

        store.addAuthority(authority);
    }

    @Test
    void checkAddingInvalidAuthority()
    {
        RDAPAuthorityStore store = new RDAPAuthorityStore();
        assertThrows(IllegalArgumentException.class, () ->
        {
            store.addAuthority(null);
        });
    }

    @Test
    void findAuthorityByName()
    {
        RDAPAuthority authority = new RDAPAuthority("apnic");
        RDAPAuthorityStore store = new RDAPAuthorityStore();

        store.addAuthority(authority);

        assertEquals(store.findAuthority("apnic"), authority);
    }

    @Test
    void findAuthorityByAlias()
    {
        RDAPAuthority authority = new RDAPAuthority("apnic");
        RDAPAuthorityStore store = new RDAPAuthorityStore();

        authority.setAliases(Collections.singletonList("apnic1"));
        store.addAuthority(authority);

        assertEquals(store.findAuthority("apnic1"), authority);
    }
}
