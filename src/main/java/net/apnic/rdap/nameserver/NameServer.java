package net.apnic.rdap.nameserver;

import net.apnic.rdap.domain.Domain;

/**
 * Represents a single name server object in RDAP
 */
public class NameServer
    extends Domain
{
    /**
     * {@inheritDoc}
     */
    public NameServer(String nameServer)
    {
        super(nameServer);
    }
}
