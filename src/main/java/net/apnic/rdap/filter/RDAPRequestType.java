package net.apnic.rdap.filter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum RDAPRequestType {
    AUTNUM("autnum"),
    DOMAIN("domain"),
    DOMAINS("domains"),
    ENTITY("entity"),
    ENTITIES("entities"),
    HELP("help"),
    HISTORY("history"),
    IP("ip"),
    NAMESERVER("nameserver"),
    NAMESERVERS("nameservers");

    private static final Map<String, RDAPRequestType> valueToTypeMap = new HashMap<>(RDAPRequestType.values().length);
    private final String pathValue;

    static {    // initialise map
        Arrays.stream(RDAPRequestType.values()).forEach(t -> valueToTypeMap.put(t.pathValue, t));
    }

    RDAPRequestType(String pathValue) {
        this.pathValue = pathValue;
    }

    public static RDAPRequestType fromPathValue(String pathValue) throws IllegalArgumentException {
        RDAPRequestType type = valueToTypeMap.get(pathValue);

        if (type == null) {
            throw new IllegalArgumentException("No RDAPRequestType for value: " + pathValue);
        }

        return type;
    }

    public String getPathValue()
    {
        return pathValue;
    }

}
