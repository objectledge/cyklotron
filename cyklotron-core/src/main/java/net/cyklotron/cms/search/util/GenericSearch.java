package net.cyklotron.cms.search.util;

import java.util.Collection;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;

public interface GenericSearch<T>
{
    Collection<T> search(PerformSearch performSearch);

    Collection<String> getAllFieldNames();

    <R> R useSearcher(SearcherUser<R> searcherUser);

    Analyzer getAnalyzer();

    Collection<Term> analyze(String fieldName, String value);
}
