package net.apnic.rdap.path.rest;

import net.apnic.rdap.client.RDAPClient;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract class for all RDAP rest controller path segments to subclass from.
 *
 * This class provides utilities that all path segment controllers will need.
 */
public abstract class PathRestController
{
    @Autowired
    private RDAPClient rdapClient;

    /**
     * Provides the RDAP client to use for proxying requests.
     *
     * @return RDAPClient to used for proxying requests.
     */
    public RDAPClient getRDAPClient()
    {
        return rdapClient;
    }
}
