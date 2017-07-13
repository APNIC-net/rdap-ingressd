package net.apnic.rdap.rdap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Class represents a notice object in the RDAP protocol.
 *
 * @see https://tools.ietf.org/html/rfc7483
 */
public class RDAPNotice {
    private final List<String> descriptions;
    private final List<RDAPLink> links;
    private final String title;

    public RDAPNotice(List<String> descriptions, List<RDAPLink> links, String title) {
        this.descriptions = Collections.unmodifiableList(
                new ArrayList<>(Optional.ofNullable(descriptions).orElse(Collections.emptyList())));
        this.links = Collections.unmodifiableList(
                new ArrayList<>(Optional.ofNullable(links).orElse(Collections.emptyList())));
        this.title = title;
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

    public RDAPNotice withContext(String context)
    {
        return new RDAPNotice(
                descriptions,
                getLinks().stream().map(rdapLink -> rdapLink.withValue(context)).collect(Collectors.toList()),
                title);
    }
}
