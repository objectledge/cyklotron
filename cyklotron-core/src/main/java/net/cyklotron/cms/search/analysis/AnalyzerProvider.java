package net.cyklotron.cms.search.analysis;

import org.apache.lucene.analysis.Analyzer;

/**
 * Provides Lucene analyzers.
 * 
 * @author Marek Lewandowski
 */
public interface AnalyzerProvider
{
    AnalyzerProvider DEFAULT_ANALYZER = new DefaultAnalyzerProvider();

    Analyzer getAnalyzer();
}
