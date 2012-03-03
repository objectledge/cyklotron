package net.cyklotron.cms.search.analysis;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.util.Version;

import net.cyklotron.cms.search.SearchConstants;

/**
 * @author <a href="mailto:damian@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryAnalyzer.java,v 1.2 2005-01-27 02:11:54 pablo Exp $
 */
public class PerFieldAnalyzer
    extends PerFieldAnalyzerWrapper
{

    /** Builds an analyzer. */
    public PerFieldAnalyzer()
    {
        super(new TextAnalyzer(Version.LUCENE_30));
        addAnalyzer(SearchConstants.FIELD_CATEGORY, new NewlineSeparatedAnalyzer());
    }

    /**
     * Builds an analyzer with defined stop words.
     * 
     * @param stopwords a Reader for loading stop word list.
     * @param stemmer stemmer to be used.
     * @throws IOException when stop words could not be loaded
     */
    public PerFieldAnalyzer(Reader stopwords, Stemmer stemmer)
        throws IOException
    {
        super(new TextAnalyzer(Version.LUCENE_30, stopwords, stemmer));
        addAnalyzer(SearchConstants.FIELD_CATEGORY, new NewlineSeparatedAnalyzer());
    }
}