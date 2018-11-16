package net.apnic.rdap.resource;

import net.apnic.rdap.authority.RDAPAuthority;

import java.util.Objects;

/**
 * Represents the mapping from a resource (IP, ANS or Domain) to a {@link net.apnic.rdap.authority.RDAPAuthority}.
 */
public class ResourceMapping<Resource> {
    final private Resource resource;
    final private RDAPAuthority authority;

    public ResourceMapping(Resource resource, RDAPAuthority authority) {
        this.resource = resource;
        this.authority = authority;
    }

    public Resource getResource() {
        return resource;
    }

    public RDAPAuthority getAuthority() {
        return authority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourceMapping<?> that = (ResourceMapping<?>) o;
        return Objects.equals(resource, that.resource) &&
                Objects.equals(authority, that.authority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resource, authority);
    }
}
