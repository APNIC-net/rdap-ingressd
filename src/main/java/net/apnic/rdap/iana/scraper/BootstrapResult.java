package net.apnic.rdap.iana.scraper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
class BootstrapResult
{
    private String description = null;
    private List<BootstrapService> services = null;
    private String version = null;

    public String getDescription()
    {
        return description;
    }

    public List<BootstrapService> getServices()
    {
        return services;
    }

    public String getVersion()
    {
        return version;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setServices(List<BootstrapService> services)
    {
        this.services = services;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }
}
