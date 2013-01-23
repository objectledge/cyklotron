package net.cyklotron.cms.search.analysis;

import org.apache.lucene.analysis.stempel.StempelStemmer;

/**
 * Stemmer for polish language using <a href="http://www.getopt.org/stempel/">Stempel</a>
 * 
 * @author rafal, marek
 */
public class StemmerPL
    implements Stemmer
{
    private final StempelStemmer stemmer;

    StemmerPL(StempelStemmer stemmer)
    {
        this.stemmer = stemmer;
    }

    /**
     * This is not effective at all. Strings should not be created. Need to refactor it later.
     */
    @Override
    public String stem(String term)
    {
        // TODO refactor this method to return
        StringBuilder stem = stemmer.stem(term);
        if(stem != null)
        {
            return stem.toString();
        }
        else
        {
            return null;
        }
    }
}
