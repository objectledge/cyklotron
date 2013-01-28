package net.cyklotron.cms.search.analysis;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.util.Version;
import org.objectledge.filesystem.FileSystem;

import net.cyklotron.cms.search.SearchConstants;

/**
 * @author <a href="mailto:damian@caltha.pl">Damian Gajda</a>
 * @author Marek Lewandowski
 * @version $Id: CategoryAnalyzer.java,v 1.3 2013-01-18 02:11:54 marek Exp $
 */
public class PerFieldAnalyzerFactory
{
    private static final Version LUCENE_VERSION = SearchConstants.LUCENE_VERSION;

    private final FileSystem fileSystem;

    private final String stopwordsEncoding;

    PerFieldAnalyzerFactory(FileSystem fileSystem, String stopwordsEncoding)
    {
        this.fileSystem = fileSystem;
        this.stopwordsEncoding = stopwordsEncoding;
    }

    public PerFieldAnalyzerFactory(FileSystem fileSystem)
    {
        this.fileSystem = fileSystem;
        this.stopwordsEncoding = "UTF-8";
    }

    /**
     * Builds an analyzer with defined stop words.
     * 
     * @param stopwords a path to stopwords
     * @param stemmer stemmer to be used.
     * @throws IOException when stop words could not be loaded
     */
    public Analyzer createPerFieldAnalyzer(String pathToStopwords, Stemmer stemmer)
        throws IOException
    {
        return new PerFieldAnalyzerWrapper(new TextAnalyzer(LUCENE_VERSION),
            getFieldCategoryAnalyzer(pathToStopwords, stemmer));
    }

    private Map<String, Analyzer> getFieldCategoryAnalyzer(String pathToStopwords,
        Stemmer stemmer)
        throws IOException
    {
        Map<String, Analyzer> analyzerPerField = new HashMap<String, Analyzer>();
        analyzerPerField.put(SearchConstants.FIELD_CATEGORY, new NewlineSeparatedAnalyzer());
        
        analyzerPerField.put(SearchConstants.FIELD_INDEX_ABBREVIATION, createTextAnalyzer(pathToStopwords, stemmer));
        analyzerPerField.put(SearchConstants.FIELD_INDEX_TITLE, createTextAnalyzer(pathToStopwords, stemmer));
        analyzerPerField.put(SearchConstants.FIELD_INDEX_CONTENT, createTextAnalyzer(pathToStopwords, stemmer));
        return analyzerPerField;
    }

    private TextAnalyzer createTextAnalyzer(String pathToStopwords, Stemmer stemmer)
        throws IOException
    {
        return new TextAnalyzer(
            LUCENE_VERSION, getStopwordsReader(pathToStopwords), stemmer);
    }

    private Reader getStopwordsReader(String path)
        throws IOException
    {
        return fileSystem.getReader(path, stopwordsEncoding);
    }

}
