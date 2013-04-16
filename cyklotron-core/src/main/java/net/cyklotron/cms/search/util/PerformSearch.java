package net.cyklotron.cms.search.util;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;

public interface PerformSearch
{
    /**
     * Use it this method to do your query. Do not close searcher, just use it and let it go out of
     * scope.
     * 
     * @param indexSearcher IndexSearcher over index.
     * @return collection of documents you want to get transformed into resources from your index
     * @throws IOException
     */
    List<Document> doSearch(IndexSearcher indexSearcher)
        throws IOException;
}
