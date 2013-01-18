package net.cyklotron.cms.search.analysis;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.util.Version;

import net.cyklotron.cms.search.SearchConstants;

/**
 * @author <a href="mailto:damian@caltha.pl">Damian Gajda</a>
 * @author Marek Lewandowski
 * @version $Id: CategoryAnalyzer.java,v 1.3 2013-01-18 02:11:54 marek Exp $
 */
public class PerFieldAnalyzer
{
    private static final Version LUCENE_VERSION = Version.LUCENE_40;

    private PerFieldAnalyzer()
    {
        // uninstantiable
    }

    /** Builds an analyzer. */
    public static Analyzer createPerFieldAnalyzer()
    {
        return new PerFieldAnalyzerWrapper(new TextAnalyzer(LUCENE_VERSION),
            getFieldCategoryAnalyzer());
    }

    /**
     * Builds an analyzer with defined stop words.
     * 
     * @param stopwords a Reader for loading stop word list.
     * @param stemmer stemmer to be used.
     * @throws IOException when stop words could not be loaded
     */
    public static Analyzer createPerFieldAnalyzer(Reader stopwords, Stemmer stemmer)
        throws IOException
    {
        return new PerFieldAnalyzerWrapper(new TextAnalyzer(LUCENE_VERSION, stopwords, stemmer),
            getFieldCategoryAnalyzer());
    }

    private static Map<String, Analyzer> getMapWithSingleEntry(String fieldName, Analyzer analyzer)
    {
        Map<String, Analyzer> analyzerPerField = new HashMap<String, Analyzer>();
        analyzerPerField.put(fieldName, analyzer);
        return analyzerPerField;
    }

    private static Map<String, Analyzer> getFieldCategoryAnalyzer()
    {
        return getMapWithSingleEntry(SearchConstants.FIELD_CATEGORY, new NewlineSeparatedAnalyzer());
    }


}
