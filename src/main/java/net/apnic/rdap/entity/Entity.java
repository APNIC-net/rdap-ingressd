package net.apnic.rdap.entity;

/**
 * Represents a single entity object in RDAP
 */
public class Entity
{
    private String entity;

    /**
     * Default construct
     *
     * Takes the entity name
     *
     * @param entity Entity name
     */
    public Entity(String entity)
    {
        this.entity = entity;
    }

    /**
     * Returns the entity name.
     *
     * @return Entity name
     */
    public String getEntity()
    {
        return entity;
    }
}
