package net.cyklotron.cms.search;

import org.apache.lucene.search.Searcher;
import org.objectledge.coral.security.Subject;

/**
 * Searching interface.
 *     
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SearchingFacility.java,v 1.2 2005-01-19 08:22:54 pablo Exp $
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
     * returns a searcher.
     *
     * @param pools the index pools.
     * @param subject searching subject.
     */
    public void returnSearcher(Searcher searcher);
}
