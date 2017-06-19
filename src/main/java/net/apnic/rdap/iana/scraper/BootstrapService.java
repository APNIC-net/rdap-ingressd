package net.apnic.rdap.iana.scraper;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single service found in a given Bootstrap file.
 */
class BootstrapService
{
    public static final int NUMBER_OF_ELEMENTS = 2;
    public static final int RESOURCES_INDEX = 0;
    public static final int SERVERS_INDEX = 1;

    private List<URI> serversByURI = null;
    private List<List<String>> serviceElements = null;

    /**
     * Constructs a new service with the the supplied service elements from the
     * bootstrap file.
     *
     * @param serviceElements Elements that comprise a single service found in a
     *                        Bootstrap file
     */
    @JsonCreator
    public BootstrapService(List<List<String>> serviceElements)
    {
        if(serviceElements == null)
        {
            throw new IllegalArgumentException("serviceElements cannot be null");
        }
        else if(serviceElements.size() != NUMBER_OF_ELEMENTS)
        {
            throw new IllegalArgumentException("To few or to many elements in service");
        }

        this.serviceElements = serviceElements;
    }

    /**
     * Returns the resources that this service depicts for a given Bootstrap
     * file.
     *
     * @return List of resources and their string representations
     */
    public List<String> getResources()
    {
        return serviceElements.get(RESOURCES_INDEX);
    }

    /**
     * Returns the server URI's where authorative information can be found for
     * the resources described by this service.
     *
     * @return List of servers and their string representation
     */
    public List<String> getServers()
    {
        return serviceElements.get(SERVERS_INDEX);
    }

    /**
     * Returns this services servers as actual URI objects.
     *
     * Users of this function must be read to catch errors as its plausible that
     * a non conformant server URI could be passed which will result in an error
     * being thrown.
     *
     * @return List of server and their URI representations
     * @throws URISyntaxException for when a URI does not conformto URI standard
     * @see getServers()
     */
    public List<URI> getServersByURI()
        throws URISyntaxException
    {
        if(serversByURI == null)
        {
            serversByURI = new ArrayList<URI>();
            for(String server : getServers())
            {
                serversByURI.add(new URI(server));
            }
        }

        return serversByURI;
    }
}
