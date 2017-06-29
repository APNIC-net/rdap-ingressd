package net.apnic.rdap.entity.filters;

import net.apnic.rdap.authority.RDAPAuthority;
import net.apnic.rdap.directory.Directory;
import net.apnic.rdap.entity.Entity;
import net.apnic.rdap.error.MalformedRequestException;
import net.apnic.rdap.filter.filters.RDAPPathRouteFilter;
import net.apnic.rdap.filter.RDAPRequestPath;
import net.apnic.rdap.filter.RDAPRequestType;
import net.apnic.rdap.resource.ResourceNotFoundException;

public class EntityRouteFilter
    extends RDAPPathRouteFilter
{
    private static final int ENTITY_PARAM_INDEX = 0;
    private static final int NO_REQUEST_PARAMS = 1;

    /**
     *
     */
    public EntityRouteFilter(Directory directory)
    {
        super(directory);
    }

    /**
     *
     */
    @Override
    public RDAPAuthority runRDAPFilter(RDAPRequestPath path)
        throws ResourceNotFoundException, MalformedRequestException
    {
        String[] args = path.getRequestParams();

        if(args.length != NO_REQUEST_PARAMS)
        {
            throw new MalformedRequestException(
                "Not enough arguments for domain path segment");
        }

        return getDirectory()
            .getEntityAuthority(new Entity(args[ENTITY_PARAM_INDEX]));
    }

    /**
     * {@inheritDocs}
     */
    @Override
    public RDAPRequestType supportedRequestType()
    {
        return RDAPRequestType.ENTITY;
    }
}
