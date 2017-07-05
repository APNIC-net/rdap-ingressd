package net.apnic.rdap.iana.scraper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Java POJO representation of the JSON returned by IANA for a bootstrap file
 * request.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
class BootstrapResult
{
    private String description = null;
    private List<BootstrapService> services = null;
    private String version = null;

    /**
     * Returns the description set in the IANA bootstrap file.
     *
     * @return Bootstrap file description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * List of all services found in the IANA bootstrap file.
     *
     * @return Bootstrap file services
     */
    public List<BootstrapService> getServices()
    {
        return services;
    }

    /**
     * The version set for the IANA bootstrap file.
     *
     * @return Bootstrap file version
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * Sets the description from the Bootstrap file.
     *
     * @param description Bootstrap description
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Sets the services found in the given Bootstrap file
     *
     * @param services Bootstrap file services
     */
    public void setServices(List<BootstrapService> services)
    {
        this.services = services;
    }

    /**
     * Sets the version from the Bootstrap file
     *
     * @param version Bootstrap version
     */
    public void setVersion(String version)
    {
        this.version = version;
    }
}
