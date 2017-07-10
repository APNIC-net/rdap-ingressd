package net.apnic.rdap.rdap;

import com.fasterxml.jackson.annotation.JsonValue;

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
