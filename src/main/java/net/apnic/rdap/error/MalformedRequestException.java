package net.apnic.rdap.error;

/**
 * Represents a malformed RDAP request.
 *
 * @see java.lang.Exception
 */
public class MalformedRequestException
    extends Exception
{
    /**
     * @see java.lang.Exception
     */
    public MalformedRequestException(String message)
    {
        super(message);
    }

    /**
     * @see java.lang.Exception
     */
    public MalformedRequestException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * @see java.lang.Exception
     */
    public MalformedRequestException(Throwable cause)
    {
        super(cause);
    }
}
