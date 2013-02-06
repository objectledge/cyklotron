package net.cyklotron.cms.search.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.jcontainer.dna.Logger;
import org.objectledge.coral.security.Subject;

import net.cyklotron.cms.search.IndexResource;
import net.cyklotron.cms.search.IndexingFacility;
import net.cyklotron.cms.search.PoolResource;
import net.cyklotron.cms.search.SearchException;
import net.cyklotron.cms.search.SearchingFacility;

import com.google.common.base.Optional;

/**
 * Implementation of Search Service
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SearchingFacilityImpl.java,v 1.7 2013-01-17 07:36:44 marek Exp $
 */
public class SearchingFacilityImpl implements SearchingFacility
{
    /** logging facility */
    private Logger log;

    /** system anonymous subject */
    private IndexingFacility indexingFacility;

    // local //////////////////////////////////////////////////////////////////////////////////////

    /**
     * Creates the facility.
     * @param log
     */
    public SearchingFacilityImpl(
        Logger log,
        IndexingFacility indexingFacility)
    {
        this.log = log;
        this.indexingFacility = indexingFacility;
    }

    @Override
    public Optional<IndexSearcher> getSearcher(PoolResource[] pools, Subject subject)
        throws SearchException
    {
        List<IndexResource> indexes = new ArrayList<>(pools.length * 8);
        for (int i = 0; i < pools.length; i++)
        {
            indexes.addAll(pools[i].getIndexes());
        }
        if (indexes.size() == 0)
        {
            log.warn("No indexes for searching defined for the chosen pool list");
        }

        return getSearcher(indexes, subject);
    }

    @Override
    public void returnSearcher(IndexSearcher searcher)
    {
        try
        {
            searcher.getIndexReader().close();
        }
        catch(IOException e)
        {
            log.error("failed to close searcher", e);
        }
    }
    
    @Override
    public void clearSearcher(IndexResource index)
    {
        // searcher pooling was removed     
    }
    
    // implementation /////////////////////////////////////////////////////////////////////////////


    private Optional<IndexSearcher> getSearcher(List<IndexResource> indexes, Subject subject)
        throws SearchException
    {
        boolean useOnlyPublic = (Subject.ANONYMOUS == subject.getId());
        
        // List<IndexSearcher> searchers = new ArrayList<>(indexes.size());
        List<IndexReader> indexReaders = new ArrayList<>(indexes.size());
        for(IndexResource index : indexes)
        {
            if(!useOnlyPublic || (useOnlyPublic && index.getPublic()))
            {
                try
                {
                    indexReaders.add(getIndexReader(index));
                }
                catch (IOException e)
                {
                    // fail but go on trying to search on correct searchers
                    log.warn("Error getting searcher for index '"+index.getPath()+"'", e);
                }
            }
        }

        if(indexReaders.size() == 0)
        {
            return Optional.absent();
        }
        
        MultiReader multiReader = new MultiReader(indexReaders.toArray(new IndexReader[indexReaders
            .size()]));
        return Optional.of(new IndexSearcher(multiReader));
    }

    private IndexReader getIndexReader(IndexResource indexResource)
        throws SearchException, IOException
    {
        Directory indexDirectory = indexingFacility.getIndexDirectory(indexResource);
        return IndexReader.open(indexDirectory);
    }
}

