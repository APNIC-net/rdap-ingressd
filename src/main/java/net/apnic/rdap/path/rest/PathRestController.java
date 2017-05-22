package net.apnic.rdap.path.rest;

import net.apnic.rdap.client.RDAPClient;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class PathRestController
{
    @Autowired
    private RDAPClient rdapClient;

    public PathRestController()
    {
    }

    public RDAPClient getRDAPClient()
    {
        return rdapClient;
    }
}
