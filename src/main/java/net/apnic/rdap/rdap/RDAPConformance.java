package net.apnic.rdap.rdap;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum to represent the different RDAP conformance levels possible.
 *
 * @see https://tools.ietf.org/html/rfc7483
 */
public enum RDAPConformance
{
    LEVEL_0("rdap_level_0");

    private String typeValue = null;

    private RDAPConformance(String typeValue)
    {
        this.typeValue= typeValue;
    }

    @JsonValue
    @Override
    public String toString()
    {
        return typeValue;
    }
}
