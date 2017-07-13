package net.apnic.rdap.rdap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class RDAPObject
{
    private final Set<RDAPConformance> conformance;
    private final List<RDAPNotice> notices;

    protected RDAPObject(Set<RDAPConformance> conformance, List<RDAPNotice> notices) {
        this.conformance = conformance;
        this.notices = notices;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("notices")
    public List<RDAPNotice> getNotices()
    {
        return notices;
    }

    @JsonProperty("rdapConformance")
    public Set<RDAPConformance> getRDAPConformance()
    {
        if(this.conformance == null)
        {
            return Collections.<RDAPConformance>emptySet();
        }
        return conformance;
    }
}
