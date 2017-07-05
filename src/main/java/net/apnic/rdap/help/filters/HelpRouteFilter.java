package net.apnic.rdap.help.filters;

import net.apnic.rdap.authority.RDAPAuthority;
import net.apnic.rdap.directory.Directory;
import net.apnic.rdap.error.MalformedRequestException;
import net.apnic.rdap.filter.filters.RDAPPathRouteFilter;
import net.apnic.rdap.filter.RDAPRequestPath;
import net.apnic.rdap.filter.RDAPRequestType;
import net.apnic.rdap.resource.ResourceNotFoundException;

public class HelpRouteFilter
    extends RDAPPathRouteFilter
{
    public HelpRouteFilter(Directory directory)
    {
        super(directory);
    }

    @Override
    public RDAPAuthority runRDAPFilter(RDAPRequestPath path)
        throws ResourceNotFoundException, MalformedRequestException
    {
        if(path.getRequestParams().length != 0)
        {
            throw new MalformedRequestException(
                "help path segment does not take and arguments");
        }

        return getDirectory().getHelpAuthority();
    }

    /**
     * {@inheritDocs}
     */
    @Override
    public RDAPRequestType supportedRequestType()
    {
        return RDAPRequestType.HELP;
    }
}
