package net.apnic.rdap.rdap;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory helper class for constructing conformant RDAP objects for client
 * requests that are contextualised and pre filled with default notices.
 */
public class RDAPObjectFactory
{
    private static final RDAPConformance DEFAULT_CONFORMANCE =
        RDAPConformance.LEVEL_0;

    private List<RDAPNotice> defaultNotices = null;

    /**
     * Provides the default notices that have been set for every created
     * RDAPObject subclass.
     *
     * @return List of default notices if set or null
     */
    public List<RDAPNotice> getDefaultNotices()
    {
        return defaultNotices;
    }

    /**
     * Sets the default notices used by this factory from a list of pre
     * constructed notices.
     *
     * @return Sets the default notices used by this factory
     */
    public void setDefaultNotices(List<RDAPNotice> notices)
    {
        defaultNotices = notices;
    }

    /**
     * Deep copies the default notices
     */
    private List<RDAPNotice> copyDefaultNoticesWithContext(String context)
    {
        if(getDefaultNotices() == null)
        {
            return getDefaultNotices();
        }

        List<RDAPNotice> noticesCopy = new ArrayList<RDAPNotice>();
        for(RDAPNotice noticeToCopy : getDefaultNotices())
        {
            noticesCopy.add(noticeToCopy.clone().setNoticeContext(context));
        }

        return noticesCopy;
    }

    /**
     * Constructs a child RDAPObject pre filled for the current context.
     *
     * Constructed objects used by this method must have a default constructor
     * with no arguments.
     *
     * @param object Child class of RDAPObject to construct and pre fill
     * @return Newly created object pre-filled
     */
    public <T extends RDAPObject> T createRDAPObject(Class<T> objectType,
                                                     String context)
    {
        try
        {
            return fillCommonObject(objectType.newInstance(), context);
        }
        catch(Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Fills a child RDAPObject with pre defined static information and context.
     *
     * @param rdapObject Child object to fill
     * @return Filled child object
     */
    private <T extends RDAPObject> T fillCommonObject(T rdapObject,
                                                      String context)
    {
        return (T)rdapObject
            .addConformance(DEFAULT_CONFORMANCE)
            .setNotices(copyDefaultNoticesWithContext(context));
    }
}
