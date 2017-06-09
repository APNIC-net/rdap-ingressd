package net.apnic.rdap.domain;

import java.math.BigInteger;

import net.ripe.ipresource.Ipv4Address;
import net.ripe.ipresource.Ipv6Address;
import net.ripe.ipresource.IpRange;

class DomainUtils
{
    private final static int ARPA4_THROW_INDEX = Domain.ARPA4_FIELD_COUNT;
    private final static int ARPA6_THROW_INDEX = Domain.ARPA6_FIELD_COUNT;
    private final static int IPV4_FIELD_BITS = 8;
    private final static int IPV6_BYTE_COUNT = 16;
    private final static int IPV6_FIELD_BITS = 4;
    private final static int MIN_IP_FIELDS = 1;
    private final static int MAX_IPV4_FIELDS = 4;
    private final static int MAX_IPV6_FIELDS = 32;

    public static IpRange ipAddressForArpaDomain(Domain domain)
        throws IllegalArgumentException
    {
        if(domain.isArpa4())
        {
            return ip4AddressForArpaDomain(domain);
        }
        else if(domain.isArpa6())
        {
            return ip6AddressForArpaDomain(domain);
        }
        else
        {
            throw new
                IllegalArgumentException("Domain must be a valid arpa domain");
        }
    }

    private static IpRange ip4AddressForArpaDomain(Domain domain)
        throws IllegalArgumentException
    {
        String[] domainFields = domain.getFields();
        long ipAddress = 0;
        int fieldsToProcess = domainFields.length - ARPA4_THROW_INDEX;

        if(fieldsToProcess < MIN_IP_FIELDS || fieldsToProcess > MAX_IPV4_FIELDS)
        {
            throw new IllegalArgumentException(
                "IPv4 arpa domain has invalid number of fields \"" +
                fieldsToProcess + "\"");
        }

        for(int i = fieldsToProcess - 1, bytePos = MAX_IPV4_FIELDS - 1;
            i >= 0; --i, --bytePos)
        {
            try
            {
                ipAddress |=
                    Byte.parseByte(domainFields[i]) << (IPV4_FIELD_BITS * bytePos);
            }
            catch(NumberFormatException ex)
            {
                throw new IllegalArgumentException("Failed to parse arpa4 field"
                    + "\"" + domainFields[i] + "\"");
            }
        }

        return IpRange.prefix(new Ipv4Address(ipAddress),
                              (fieldsToProcess) * IPV4_FIELD_BITS);
    }

    private static IpRange ip6AddressForArpaDomain(Domain domain)
        throws IllegalArgumentException
    {
        String[] domainFields = domain.getFields();
        byte[] ipAddress = new byte[IPV6_BYTE_COUNT];
        int fieldsToProcess = domainFields.length - ARPA6_THROW_INDEX;

        if(fieldsToProcess < MIN_IP_FIELDS || fieldsToProcess > MAX_IPV6_FIELDS)
        {
            throw new IllegalArgumentException(
                "IPv6 arpa domain has invalid number of fields \"" +
                fieldsToProcess + "\"");
        }

        // Very ugly
        for(int i = fieldsToProcess - 1, bytePos = 0, nibblePos = 1;
            i >= 0; --i, bytePos += (nibblePos == 0 ? 1 : 0), nibblePos ^= 1)
        {
            try
            {
                ipAddress[bytePos] |=
                    Byte.parseByte(domainFields[i], 16) << (IPV6_FIELD_BITS * nibblePos);
            }
            catch(NumberFormatException ex)
            {
                throw new IllegalArgumentException("Failed to parse arpa6 field"
                    + "\"" + domainFields[i] + "\"");
            }
        }

        return IpRange.prefix(new Ipv6Address(new BigInteger(1, ipAddress)),
                              (fieldsToProcess) * IPV6_FIELD_BITS);
    }
}
