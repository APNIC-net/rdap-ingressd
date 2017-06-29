package net.apnic.rdap.domain;

public class Domain
{
    private final static String DOMAIN_SPLIT_REGEX = "\\.";
    private final static char DOMAIN_START = '.';

    public final static String ARPA_DOMAIN = "arpa.";
    public final static String ARPA4_DOMAIN = "in-addr.arpa.";
    public final static int ARPA4_FIELD_COUNT = 2;
    public final static String ARPA6_DOMAIN = "ip6.arpa.";
    public final static int ARPA6_FIELD_COUNT = 2;

    private String domain;
    private String[] fields = null;

    public Domain(String domain)
        throws IllegalArgumentException
    {
        if(domain == null || domain.isEmpty())
        {
            throw new IllegalArgumentException(
                "domain name can not be null or empty");
        }

        if(domain.charAt(domain.length() - 1) != DOMAIN_START)
        {
            domain += DOMAIN_START;
        }

        this.domain = domain;
    }

    public String getDomain()
    {
        return domain;
    }

    public String[] getFields()
    {
        if(fields == null)
        {
            fields = getDomain().split(DOMAIN_SPLIT_REGEX);
        }
        return fields;
    }

    public String getTLD()
    {
        String[] fields = getFields();
        return fields[fields.length - 1];
    }

    public boolean isArpa()
    {
        return getDomain().endsWith(ARPA4_DOMAIN) ||
               getDomain().endsWith(ARPA6_DOMAIN);
    }

    public boolean isArpa4()
    {
        return getDomain().endsWith(ARPA4_DOMAIN);
    }

    public boolean isArpa6()
    {
        return getDomain().endsWith(ARPA6_DOMAIN);
    }
}
