package net.apnic.rdap.authority.routing;

public enum RoutingAction
{
    PROXY("proxy"),
    REDIRECT("redirect"),
    REDIRECT_WITH_FALLBACK("redirect_with_fallback");

    private String value = null;

    private RoutingAction(String value)
    {
        this.value = value;
    }

    public static RoutingAction getEnum(String valueStr)
    {
        for(RoutingAction type : values())
        {
            if(type.getValue().equals(valueStr))
            {
                return type;
            }
        }
        throw new IllegalArgumentException("No RoutingType for value");
    }

    public String getValue()
    {
        return value;
    }

    public String toString()
    {
        return getValue();
    }
}
