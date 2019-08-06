package net.apnic.rdap.filter.config;

public enum RequestContextKeys {
    RDAP_REQUEST_PATH,
    RDAP_AUTHORITY;

    public String getKey() {
        return toString();
    }
}
