package net.cyklotron.cms.search.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiSearcher;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.store.Directory;
import org.jcontainer.dna.Logger;
import org.objectledge.coral.security.Subject;

import net.cyklotron.cms.search.IndexResource;
import net.cyklotron.cms.search.IndexingFacility;
import net.cyklotron.cms.search.PoolResource;
import net.cyklotron.cms.search.SearchException;
import net.cyklotron.cms.search.SearchingFacility;

/**
 * Implementation of Search Service
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SearchingFacilityImpl.java,v 1.6 2005-05-30 07:36:44 rafal Exp $
 */
public class SearchingFacilityImpl implements SearchingFacility
{
    /** logging facility */
    private Logger log;

    /** system anonymous subject */
    private IndexingFacility indexingFacility;

    // local //////////////////////////////////////////////////////////////////////////////////////

    private Map indexSearchersCache = new WeakHashMap(); 
    
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

    public Searcher getSearcher(PoolResource[] pools, Subject subject) throws SearchException
    {
        List indexes = new ArrayList(pools.length * 8);
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

    public void returnSearcher(Searcher searcher)
    {
        // WARN: we do not close the searcher - we leave it open for future use.
    }

    // implementation /////////////////////////////////////////////////////////////////////////////

    private Searcher getSearcher(List indexes, Subject subject) throws SearchException
    {
        boolean useOnlyPublic = (Subject.ANONYMOUS == subject.getId());
        
        List searchers = new ArrayList(indexes.size());
        for (int i = 0; i < indexes.size(); i++)
        {
            IndexResource index = (IndexResource) (indexes.get(i));
            if(!useOnlyPublic || (useOnlyPublic && index.getPublic()))
            {
                try
                {
                    searchers.add(getSearcher(index));
                }
                catch (IOException e)
                {
                    // fail but go on trying to search on correct searchers
                    log.warn("Error getting searcher for index '"+index.getPath()+"'", e);
                }
            }
        }

        if(searchers.size() == 0)
        {
            return new NullSearcher();
        }
        
        try
        {
            Searcher[] searchables = new Searcher[searchers.size()];
            searchables = (Searcher[]) (searchers.toArray(searchables));
            Searcher searcher = new MultiSearcher(searchables);
            return searcher;
        }
        catch (IOException e)
        {
            throw new SearchException("Cannot create mulisearcher", e);
        }
    }

    private Searcher getSearcher(IndexResource index) throws IOException, SearchException
    {
        IndexSearcherDescriptor searcherDescriptor = 
            (IndexSearcherDescriptor) indexSearchersCache.get(index);

        Directory indexDirectory = indexingFacility.getIndexDirectory(index);
        long currentVersion = IndexReader.getCurrentVersion(indexDirectory);

        if(searcherDescriptor == null)
        {
            searcherDescriptor = new IndexSearcherDescriptor(index, IndexReader
                .getCurrentVersion(indexDirectory), new IndexSearcher(indexDirectory, true));
            indexSearchersCache.put(index, searcherDescriptor);
        }
        // /if the index has changed since this Searcher was created
        else if(currentVersion != searcherDescriptor.lastKnownVersion)
        {
            searcherDescriptor.update(currentVersion, new IndexSearcher(indexDirectory, true));
        }

        return searcherDescriptor.getSearcher();
    }
    
    public void clearSearcher(IndexResource index)
    {
        indexSearchersCache.remove(index);
    }    

    final class IndexSearcherDescriptor
    {
        private IndexResource index;
        private long lastKnownVersion;
        private Searcher searcher;

        public IndexSearcherDescriptor(
            IndexResource index, long lastKnownVersion, IndexSearcher searcher)
        {
            this.index = index;
            this.lastKnownVersion = lastKnownVersion;
            this.searcher = searcher;
        }

        public void update(long lastKnownVersion, IndexSearcher searcher)
        {
            this.lastKnownVersion = lastKnownVersion;
            this.searcher = searcher;
        }

        public Searcher getSearcher()
        {
            return searcher;
        }
    }
}

