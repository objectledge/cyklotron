package net.cyklotron.cms.search;

import net.labeo.services.resource.Subject;

import org.apache.lucene.search.Searcher;

/**
 * Searching interface.
 *     
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SearchingFacility.java,v 1.1 2005-01-12 20:44:36 pablo Exp $
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
