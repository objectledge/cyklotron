package net.cyklotron.cms.locations;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Provides access to a collection of location descriptors.
 * 
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: DocumentService.java,v 1.4 2007-11-18 21:23:07 rafal Exp $
 */
public interface LocationDatabaseService
{
    /**
     * Update Locations data from source.
     */
    public void update();

    /**
     * Searches for locations with specified characteristics.
     * <p>
     * The field selected with requestedField parameter is searched using prefix match. All other
     * fields are either matched exactly, or ignored when empty.
     * </p>
     * <p>
     * This method logs and quenches IOExceptions and returns empty results, when index access
     * problems occur.
     * </p>
     * 
     * @param requestedField name of the requested field
     * @param fieldValues user supplied field values.
     * @return list of locations sorted by relevance.
     */
    public List<Location> getLocations(String requestedField, Map<String, String> fieldValues);

    /**
     * Searches for coarse-grained locations (areas) matching specific name prefix.
     * 
     * @param query name prefix.
     * @param enclosingArea TODO
     * @param level maximum areaLevel to be matched
     * @return list of locations sorted by relevance.
     */
    public List<Location> getAreas(String query, String enclosingArea, int level, int limit);

    /**
     * Returns all terms in given field of location index.
     * 
     * @param field name of the requested field.
     * @return list of distinct terms in the given field;
     */
    public List<String> getAllTerms(String field);

    /**
     * Checks whether a Location exists with given field exactly matching a value.
     * 
     * @param field name of the requested field.
     * @param value field value.
     * @return boolean if at least one exact match exits.
     * @throws IOException on index access problems.
     */
    public boolean exactMatchExists(String field, String value)
        throws IOException;

    /**
     * Returns most specific location enclosing both given locations.
     * 
     * @param location1 first location.
     * @param location2 second location.
     * @return most specific location enclosing both given locations.
     */
    public Location merge(Location location1, Location location2);
}
