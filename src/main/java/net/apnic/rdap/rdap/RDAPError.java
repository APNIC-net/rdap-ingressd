package net.apnic.rdap.rdap;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Set;

/**
 * Class represent an error response in the RDAP protocol.
 *
 * @see https://tools.ietf.org/html/rfc7483
 */
public class RDAPError
    extends RDAPObject
{
    private final List<String> description;
    private final String errorCode;
    private final String title;

    public RDAPError(Set<RDAPConformance> conformance,
                     List<RDAPNotice> notices,
                     List<String> description,
                     String errorCode,
                     String title) {
        super(conformance, notices);
        this.description = description;
        this.errorCode = errorCode;
        this.title = title;
    }

    /**
     * Provides a list of description lines set for this object.
     *
     * If no descriptions have been set then this function returns null.
     *
     * @return Descriptions lines set on this error
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<String> getDescription()
    {
        return description;
    }

    /**
     * Provides the error code set for this object.
     *
     * If no error code has been set than this function returns null.
     *
     * @return Error code set on this object
     */
    public String getErrorCode()
    {
        return errorCode;
    }

    /**
     * Provides the title set for this object.
     *
     * If no title has been set than this function returns null.
     *
     * @return Title set on this object
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getTitle()
    {
        return title;
    }
}
