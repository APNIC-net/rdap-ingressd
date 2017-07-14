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
     * Constructs a new RDAPError object for the provided context.
     */
    public RDAPError createErrorObject(String context, List<String> description,
                                       String errorCode, String title)
    {
        return new RDAPError(DEFAULT_CONFORMANCE,
                             copyDefaultNoticesWithContext(context),
                             description, errorCode, title);
    }
}
