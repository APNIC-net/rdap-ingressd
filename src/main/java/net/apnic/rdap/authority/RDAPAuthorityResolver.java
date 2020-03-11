package net.apnic.rdap.authority;

import net.apnic.rdap.autnum.AsnRange;
import net.apnic.rdap.directory.Directory;
import net.apnic.rdap.domain.Domain;
import net.apnic.rdap.entity.Entity;
import net.apnic.rdap.error.MalformedRequestException;
import net.apnic.rdap.filter.RDAPRequestPath;
import net.apnic.rdap.filter.RDAPRequestType;
import net.apnic.rdap.nameserver.NameServer;
import net.apnic.rdap.resource.ResourceNotFoundException;
import net.ripe.ipresource.IpAddress;
import net.ripe.ipresource.IpRange;
import net.ripe.ipresource.IpResourceType;

/**
 * Provides methods for resolving the {@link RDAPAuthority} from a {@link RDAPRequestPath}.
 */
@FunctionalInterface
public interface RDAPAuthorityResolver {

    RDAPAuthority resolve(RDAPRequestPath path, Directory directory)
            throws ResourceNotFoundException, MalformedRequestException;

    static RDAPAuthorityResolver forType(RDAPRequestType type) {
        switch (type) {
            case AUTNUM: return RDAPAuthorityResolver::resolveAutnum;
            case DOMAIN: return RDAPAuthorityResolver::resolveDomain;
            case DOMAINS: return RDAPAuthorityResolver::resolveDomains;
            case ENTITIES: return RDAPAuthorityResolver::resolveEntities;
            case ENTITY: return RDAPAuthorityResolver::resolveEntity;
            case HELP: return RDAPAuthorityResolver::resolveHelp;
            case IP: return RDAPAuthorityResolver::resolveIp;
            case NAMESERVER: return RDAPAuthorityResolver::resolveNameserver;
            case NAMESERVERS: return RDAPAuthorityResolver::resolveNameservers;
            default: throw new IllegalArgumentException("Missing RDAPRequestType: " + type);
        }
    }

    static RDAPAuthority resolveAutnum(RDAPRequestPath path, Directory directory)
            throws MalformedRequestException, ResourceNotFoundException {
        String[] args = path.getRequestParams();

        if (args.length != 1) {
            throw new MalformedRequestException("Not enough arguments for autnum path segment");
        }

        try {
            return directory.getAutnumAuthority(AsnRange.parse(args[0]));
        } catch(IllegalArgumentException ex) {
            throw new MalformedRequestException(ex);
        }
    }

    int DOMAIN_PARAM_INDEX = 0;
    int NO_REQUEST_PARAMS = 1;

    static RDAPAuthority resolveDomain(RDAPRequestPath path, Directory directory)
            throws MalformedRequestException, ResourceNotFoundException {
        String[] args = path.getRequestParams();

        if (args.length != NO_REQUEST_PARAMS) {
            throw new MalformedRequestException("Not enough arguments for domain path segment");
        }

        try {
            return directory.getDomainAuthority(new Domain(args[DOMAIN_PARAM_INDEX]));
        } catch(IllegalArgumentException ex) {
            throw new MalformedRequestException(ex);
        }
    }

    static RDAPAuthority resolveDomains(RDAPRequestPath path, Directory directory)
            throws MalformedRequestException, ResourceNotFoundException {
        if (path.getRequestParams().length > 0) {
            throw new MalformedRequestException("Invalid path for domain search path segment.");
        }

        if (path.getQueryString() == null || path.getQueryString().isEmpty()) {
            throw new MalformedRequestException("Missing query string for domain search.");
        }

        return directory.getSearchPathAuthority();
    }

    static RDAPAuthority resolveEntities(RDAPRequestPath path, Directory directory)
            throws MalformedRequestException, ResourceNotFoundException {
        if (path.getRequestParams().length > 0) {
            throw new MalformedRequestException("Invalid path for entity search path segment.");
        }

        if (path.getQueryString() == null || path.getQueryString().isEmpty()) {
            throw new MalformedRequestException("Missing query string for entity search.");
        }

        return directory.getSearchPathAuthority();
    }

    int ENTITY_PARAM_INDEX = 0;

    static RDAPAuthority resolveEntity(RDAPRequestPath path, Directory directory)
            throws MalformedRequestException, ResourceNotFoundException {
        String[] args = path.getRequestParams();

        if (args.length != NO_REQUEST_PARAMS) {
            throw new MalformedRequestException("Not enough arguments for entity path segment");
        }

        return directory.getEntityAuthority(new Entity(args[ENTITY_PARAM_INDEX]));
    }

    static RDAPAuthority resolveHelp(RDAPRequestPath path, Directory directory)
            throws MalformedRequestException, ResourceNotFoundException {
        if (path.getRequestParams().length != 0) {
            throw new MalformedRequestException("help path segment does not take and arguments");
        }

        return directory.getHelpAuthority();
    }

    static RDAPAuthority resolveIp(RDAPRequestPath path, Directory directory)
            throws MalformedRequestException, ResourceNotFoundException {
        String[] args = path.getRequestParams();

        if (args.length == 0 || args.length > 2) {
            throw new MalformedRequestException("Not enough arguments for ip path segment");
        }

        try {
            IpAddress address = IpAddress.parse(args[0]);
            int prefixLength = address.getType() == IpResourceType.IPv4 ?
                    IpResourceType.IPv4.getBitSize() :
                    IpResourceType.IPv6.getBitSize();

            if (args.length == 2) {
                prefixLength = Integer.parseInt(args[1]);
            }

            return directory.getIPAuthority(IpRange.prefix(address, prefixLength));
        } catch (IllegalArgumentException ex) {
            throw new MalformedRequestException(ex);
        }
    }

    int NS_PARAM_INDEX = 0;

    static RDAPAuthority resolveNameserver(RDAPRequestPath path, Directory directory)
            throws MalformedRequestException, ResourceNotFoundException {
        String[] args = path.getRequestParams();

        if (args.length != NO_REQUEST_PARAMS) {
            throw new MalformedRequestException("Not enough arguments for domain path segment");
        }

        return directory.getNameServerAuthority(new NameServer(args[NS_PARAM_INDEX]));
    }

    static RDAPAuthority resolveNameservers(RDAPRequestPath path, Directory directory)
            throws MalformedRequestException, ResourceNotFoundException {
        if (path.getRequestParams().length != NO_REQUEST_PARAMS) {
            throw new MalformedRequestException("Not enough arguments for nameserver search path segment");
        }

        return directory.getSearchPathAuthority();
    }
}
