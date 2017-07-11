package net.apnic.rdap.rdap;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

/**
 * Class represent an error response in the RDAP protocol.
 *
 * @see https://tools.ietf.org/html/rfc7483
 */
public class RDAPError
    extends RDAPObject
{
    private List<String> description = null;
    private String errorCode = null;
    private String title = null;

    /**
     * Adds another description line to this error.
     *
     * @param description Description line to add
     * @return This object for chainable calls
     */
    public RDAPError addDescription(String description)
    {
        if(this.description == null)
        {
            this.description = new ArrayList<String>();
        }
        this.description.add(description);
        return this;
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

    /**
     * Sets the error code for this object and converts the supplied int to a
     * string for RDAP.
     *
     * @param errorCode Error code to set
     * @return This object for chainable calls
     */
    public RDAPError setErrorCode(int errorCode)
    {
        this.errorCode = Integer.toString(errorCode);
        return this;
    }

    /**
     * Sets the title for this RDAPError object.
     *
     * @param title Title to set
     * @return This object for chainable calls
     */
    public RDAPError setTitle(String title)
    {
        this.title = title;
        return this;
    }
}
