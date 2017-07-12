package net.apnic.rdap.rdap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Class represents a notice object in the RDAP protocol.
 *
 * @see https://tools.ietf.org/html/rfc7483
 */
public class RDAPNotice
    implements Cloneable
{
    private List<String> descriptions = null;
    private List<RDAPLink> links = null;
    private String title = null;

    public RDAPNotice addDescription(String description)
    {
        if(descriptions == null)
        {
            descriptions = new ArrayList<String>();
        }
        descriptions.add(description);
        return this;
    }

    public RDAPNotice addLink(RDAPLink link)
    {
        if(links == null)
        {
            links = new ArrayList<RDAPLink>();
        }
        links.add(link);
        return this;
    }

    public RDAPNotice clone()
    {
        RDAPNotice notice = new RDAPNotice();
        notice.setDescription(getDescriptions());
        notice.setTitle(getTitle());

        if(getLinks() != null)
        {
            List<RDAPLink> links = new ArrayList<RDAPLink>();
            for(RDAPLink link: getLinks())
            {
                links.add(link.clone());
            }
            notice.setLinks(links);
        }

        return notice;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("description")
    public List<String> getDescriptions()
    {
        return descriptions;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("links")
    public List<RDAPLink> getLinks()
    {
        return links;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("title")
    public String getTitle()
    {
        return title;
    }

    public RDAPNotice setNoticeContext(String context)
    {
        if(getLinks() == null)
        {
            return this;
        }

        for(RDAPLink link : getLinks())
        {
            link.setValue(context);
        }
        return this;
    }

    public RDAPNotice setDescription(List<String> descriptions)
    {
        this.descriptions = descriptions;
        return this;
    }

    public RDAPNotice setLinks(List<RDAPLink> links)
    {
        this.links = links;
        return this;
    }

    public RDAPNotice setTitle(String title)
    {
        this.title = title;
        return this;
    }
}
