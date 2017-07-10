package net.apnic.rdap.rdap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public abstract class RDAPObject
{
    private EnumSet<RDAPConformance> conformance = null;
    private List<RDAPNotice> notices = null;

    public RDAPObject addConformance(RDAPConformance conformance)
    {
        if(this.conformance == null)
        {
            this.conformance = EnumSet.of(conformance);
        }
        else
        {
            this.conformance.add(conformance);
        }

        return this;
    }

    public RDAPObject addNotice(RDAPNotice notice)
    {
        if(notices == null)
        {
            notices = new ArrayList<RDAPNotice>();
        }
        notices.add(notice);
        return this;
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

    public RDAPObject setNotices(List<RDAPNotice> notices)
    {
        this.notices = notices;
        return this;
    }
}
