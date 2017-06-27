package net.apnic.rdap.authority;

import java.net.URI;
import java.util.List;

public interface RDAPAuthorityEventListener
{
    public void authorityAliasesAdded(RDAPAuthority authority,
                                      List<String> addedAliases);

    public void authorityServersAdded(RDAPAuthority authority,
                                      List<URI> addServers);
}
