package net.apnic.rdap.filter;

public enum RDAPRequestType
{
    AUTNUM("autnum"),
    DOMAIN("domain"),
    DOMAINS("domains"),
    ENTITY("entity"),
    ENTITIES("entities"),
    HELP("help"),
    IP("ip"),
    NAMESERVER("nameserver"),
    NAMESERVERS("nameservers");

    private final String typeValue;

    private RDAPRequestType(String typeValue)
    {
        this.typeValue = typeValue;
    }

    public static RDAPRequestType getEnum(String valueStr)
    {
        for(RDAPRequestType type : values())
        {
            if(type.getValue().equals(valueStr))
            {
                return type;
            }
        }
        throw new IllegalArgumentException("No RDAPRequestType for value");
    }

    public String getValue()
    {
        return typeValue;
    }

    @Override
    public String toString()
    {
        return getValue();
    }
}
