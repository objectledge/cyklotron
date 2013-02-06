package net.cyklotron.cms.search.util;

import java.util.Collection;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;

public interface PerformSearch
{
    Collection<Document> doSearch(IndexSearcher searcher);
}
