package net.apnic.rdap.resource;

import net.apnic.rdap.authority.RDAPAuthority;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class StaticResourceLocatorTest
{
    @Test
    void checkDefaultConstructor()
    {
        StaticResourceLocator<Void> srl = new StaticResourceLocator<Void>();
        assertEquals(srl.getAuthority(), null);
    }

    @Test
    void checkRDAPAuthorityConstructor()
    {
        RDAPAuthority authority = new RDAPAuthority("apnic");
        StaticResourceLocator<Void> srl =
            new StaticResourceLocator<Void>(authority);

        assertEquals(srl.getAuthority(), authority);
    }

    @Test
    void checkAuthorityForResource()
        throws Exception
    {
        RDAPAuthority authority = new RDAPAuthority("apnic");
        StaticResourceLocator<Void> srl1 =
            new StaticResourceLocator<Void>(authority);
        StaticResourceLocator<Void> srl2 = new StaticResourceLocator<Void>();

        assertEquals(srl1.authorityForResource(null), authority);

        assertThrows(ResourceNotFoundException.class, () ->
        {
            srl2.authorityForResource(null);
        });
    }
}
