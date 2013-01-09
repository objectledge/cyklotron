package net.cyklotron.cms.locations;

import java.util.Collection;
import java.util.Set;

public interface LocationsProvider
{
    /**
     * Returns the list of fields defined by this provider, from most specific to least specific.
     * <p>
     * Field returned at index 0 is the most specific field used to determine equality/hashcode for
     * {@link Location} objects.
     * </p>
     * 
     * @return
     */
    public String[] getFields();

    /**
     * Returns options for a specific field.
     * 
     * @param field
     * @return
     */
    public Set<FieldOptions> getOptions(String field);

    /**
     * Return all locations in the database, updated from source.
     * 
     * @return TODO
     */
    public Collection<Location> fromSource();

    /**
     * Return all locations in the database.
     * 
     * @return
     */
    public Collection<Location> fromCache();

    /**
     * Indexing/searching options for location fields.
     */
    public enum FieldOptions
    {
        /** Field should be treated as single token. */
        NOT_ANALYZED,

        /** A subquery should be used for flexible matching of fields terms. */
        MULTI_TERM_SUBQUERY
    }
}
