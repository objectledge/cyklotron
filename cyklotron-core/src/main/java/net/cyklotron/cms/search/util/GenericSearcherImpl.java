package net.cyklotron.cms.search.util;

import java.io.IOException;
import java.util.Collection;

import org.apache.lucene.analysis.Analyzer;
import org.objectledge.coral.BackendException;
import org.objectledge.coral.store.Resource;

/**
 * Immutable wrapper around GenericIndex which provides search methods over index
 * 
 * @author Marek Lewandowski
 * @param <T> type of resource
 */
public class GenericSearcherImpl<T extends Resource, U>
    implements GenericSearch<U>
{
    private final GenericIndex<T, U> index;

    public GenericSearcherImpl(GenericIndex<T, U> index)
    {
        this.index = index;
    }

    @Override
    public Collection<U> search(PerformSearch performSearch)
    {
        try
        {
            return index.search(performSearch);
        }
        catch(IOException e)
        {
            // to be changed to empty collection
            throw new BackendException("Error during searching", e);
        }
    }

    @Override
    public Collection<String> getAllFieldNames()
    {
        try
        {
            return index.getAllFieldsNames();
        }
        catch(IOException e)
        {
            throw new BackendException("Error during fetching fieldNames from index", e);
        }
    }

    @Override
    public <R> R useSearcher(SearcherUser<R> searcherUser)
    {
        try
        {
            return index.useSearcher(searcherUser);
        }
        catch(IOException e)
        {
            throw new BackendException("Error during searching", e);
        }
    }

    @Override
    public Analyzer getAnalyzer()
    {
        return index.getAnalyzer();
    }
}
