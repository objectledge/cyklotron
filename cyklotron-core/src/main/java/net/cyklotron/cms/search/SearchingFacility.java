package net.cyklotron.cms.search;

import org.apache.lucene.search.Searcher;
import org.objectledge.coral.security.Subject;

/**
 * Searching interface.
 *     
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SearchingFacility.java,v 1.3 2005-02-09 19:22:28 rafal Exp $
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
    public Searcher getSearcher(PoolResource[] pools, Subject subject)
        throws SearchException;

    /**
     * returns a searcher to the searcher pool.
     *
     * @param searcher the Searcher.
     */
    public void returnSearcher(Searcher searcher);
}
