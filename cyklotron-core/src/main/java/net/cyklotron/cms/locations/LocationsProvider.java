package net.cyklotron.cms.locations;

import java.util.Collection;
import java.util.Set;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Sort;

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
     * Returns fine-grained location record marker. Fine grained locations MUST have this term,
     * coarse grained locations (areas) MUST NOT have this term. When {@code null} is returned, all
     * locations are considered to be fine grained.
     */
    public Term getFineGrainedLocationMarker();

    /**
     * Returns sort order provider for coarse grained locations (areas).
     */
    public Sort getCoarseGrainedLocationSort();

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
        /** Field is an integer number */
        INTEGER,

        /** Field should be treated as single token. */
        NOT_ANALYZED,

        /** A subquery should be used for flexible matching of fields terms. */
        MULTI_TERM_SUBQUERY
    }
}
