package net.cyklotron.cms.search.searching;

import net.labeo.services.table.TableState;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;

/**
 * An interface for query building method definition.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SearchMethod.java,v 1.1 2005-01-12 20:44:40 pablo Exp $
 */
public interface SearchMethod
{
    /** Gets a lucene query object representing this search method.
     *
     * @return a query object produced by this search method
     * @throws Exception on error while creating the query
     */
    public Query getQuery()
    throws Exception;

    /** Gets a human readable string representation of this methods query.
     *
     * @return a string representation of the query
     */
    public String getQueryString();

    /** Gets a human readable string representation of this methods query if an errorneous query
     * was inputed.
     *
     * @return a string representation of the query
     */
    public String getErrorQueryString();
    
    /** Sets up a table state for this method's results.
     *
     * @param state the table state to be modified
     */
    public void setupTableState(TableState state);
    
    /** Returns sorting defined for this search.
     *
     * @return an array of sort fields 
     */
    public SortField[] getSortFields();
}
