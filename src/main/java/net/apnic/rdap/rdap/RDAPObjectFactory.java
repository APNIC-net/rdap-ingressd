package net.apnic.rdap.rdap;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Factory helper class for constructing conformant RDAP objects for client
 * requests that are contextualised and pre filled with default notices.
 */
public class RDAPObjectFactory
{
    private static final Set<RDAPConformance> DEFAULT_CONFORMANCE = EnumSet.of(RDAPConformance.LEVEL_0);

    private final List<RDAPNotice> defaultNotices;

    public RDAPObjectFactory(List<RDAPNotice> defaultNotices) {
        this.defaultNotices = Collections.unmodifiableList(
                new ArrayList<>(Optional.ofNullable(defaultNotices).orElse(Collections.emptyList()))
        );
    }

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
     * Deep copies the default notices
     */
    private List<RDAPNotice> copyDefaultNoticesWithContext(String context)
    {
        return getDefaultNotices().stream().map(rdapNotice -> {
            return rdapNotice.withContext(context);
        }).collect(Collectors.toList());
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
    public <T extends RDAPObject> T createRDAPObject(Class<T> objectType, String context, Object... args)
    {
        //A bit silly. Imo if you can pass in an explicit class object, you may as well just call that class's constructor directly
        if (objectType.isAssignableFrom(RDAPError.class)) {
            List<String> description = (List<String>) args[0];
            String errorCode = (String) args[1];
            String title = (String) args[2];
            return (T) new RDAPError(DEFAULT_CONFORMANCE, copyDefaultNoticesWithContext(context), description, errorCode, title);
        }

        throw new RuntimeException("Couldn't create object for type " + objectType);
    }
}
