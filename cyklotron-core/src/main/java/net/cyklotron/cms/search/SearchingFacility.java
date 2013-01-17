package net.cyklotron.cms.search;

import org.apache.lucene.search.IndexSearcher;
import org.objectledge.coral.security.Subject;

/**
 * Searching interface.
 *     
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SearchingFacility.java,v 1.4 2005-05-30 07:36:50 rafal Exp $
 */
public interface SearchingFacility
{
    /**
     * creates a searcher for given index pools and subject.
     *
     * @param pools the index pools.
     * @param subject searching subject.
     * @throws SearchException
     */
    public IndexSearcher getSearcher(PoolResource[] pools, Subject subject)
        throws SearchException;

    /**
     * returns a searcher to the searcher pool.
     * 
     * @param searcher the IndexSearcher.
     */
    public void returnSearcher(IndexSearcher searcher);

    /**
     * Removes a searcher associated with a given index from cache.
     * 
     * @param index an index resource associated with a searcher.
     */
    public void clearSearcher(IndexResource index);
}
