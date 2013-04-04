package net.cyklotron.cms.search.util;

import java.io.IOException;

import org.apache.lucene.search.IndexSearcher;

/**
 * Use when you care only about IndexSearcher result docs.
 * 
 * @author Marek Lewandowski
 * @param <R> type of result of operation
 */
public interface SearcherUser<R>
{
    /**
     * Use searcher to run some query, do not close searcher. Operation can return nothing
     * 
     * @param indexSearcher
     * @throws IOException
     */
    R useSearcher(final IndexSearcher indexSearcher)
        throws IOException;
}
