package net.cyklotron.cms.search.analysis;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import net.cyklotron.cms.search.SearchConstants;

/**
 * Default Analyzer Provider, provides Lucene's StandardAnalyzer
 * 
 * @author Marek Lewandowski
 */
class DefaultAnalyzerProvider
    implements AnalyzerProvider
{

    @Override
    public Analyzer getAnalyzer()
    {
        return new StandardAnalyzer(SearchConstants.LUCENE_VERSION);
    }

}
