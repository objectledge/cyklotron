package net.cyklotron.cms.search.analysis;

/**
 * A stemmer for a specific language.
 * 
 * @author rafal, marek
 */
public interface Stemmer
{
    /**
     * Returns a stem for a term or null if Stempel could not stem this term.
     * 
     * @param term the term.
     * @return the stem of the term. If stem could not be determined whole word should be retured.
     */
    public String stem(String term);
}
