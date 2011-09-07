package net.cyklotron.cms.search.analysis;

/**
 * A stemmer for a specific language.
 * 
 * @author rafal
 */
public interface Stemmer
{
    /**
     * Returns a stem for a term.
     * 
     * @param term the term.
     * @return the stem of the term. If stem could not be determined whole word should be retured.
     */
    public String stem(String term);
}
