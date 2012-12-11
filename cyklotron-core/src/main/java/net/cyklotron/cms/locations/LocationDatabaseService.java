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
}
