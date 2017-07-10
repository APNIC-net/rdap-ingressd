package net.apnic.rdap.rdap;

import java.util.List;

public class RDAPObjectFactory
{
    private static final RDAPConformance DEFAULT_CONFORMANCE =
        RDAPConformance.LEVEL_0;

    private List<RDAPNotice> defaultNotices = null;

    public List<RDAPNotice> getDefaultNotices()
    {
        return defaultNotices;
    }

    public void setDefaultNotices(List<RDAPNotice> notices)
    {
        defaultNotices = notices;
    }

    public <T extends RDAPObject> T createRDAPObject(Class<T> objectType)
    {
        try
        {
            return fillCommonObject(objectType.newInstance());
        }
        catch(Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    private <T extends RDAPObject> T fillCommonObject(T rdapObject)
    {
        return (T)rdapObject
            .addConformance(DEFAULT_CONFORMANCE)
            .setNotices(defaultNotices);
    }
}
