package net.cyklotron.cms.search.internal;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.search.Weight;

/**
 * Null searcher never returns any search results.
 * 
 * It is used as a stop-gap measure when search is performed on a index pool that contains
 * no actual indexes.
 * 
 * @author rafal
 */
public class NullSearcher
    extends Searcher
{

    @Override
    public void close()
        throws IOException
    {
    }

    @Override
    public Document doc(int i)
        throws CorruptIndexException, IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Document doc(int docid, FieldSelector fieldSelector)
        throws CorruptIndexException, IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public int docFreq(Term term)
        throws IOException
    {
        return 0;
    }

    @Override
    public Explanation explain(Weight weight, int doc)
        throws IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public int maxDoc()
        throws IOException
    {
        return 0;
    }

    @Override
    public Query rewrite(Query query)
        throws IOException
    {
        return query;
    }

    @Override
    public void search(Weight weight, Filter filter, Collector results)
        throws IOException
    {
    }

    @Override
    public TopDocs search(Weight weight, Filter filter, int n)
        throws IOException
    {
        return new TopDocs(0, new ScoreDoc[0], 0);
    }

    @Override
    public TopFieldDocs search(Weight weight, Filter filter, int n, Sort sort)
        throws IOException
    {        
        return new TopFieldDocs(0, new ScoreDoc[0], null, 0);
    }
}
