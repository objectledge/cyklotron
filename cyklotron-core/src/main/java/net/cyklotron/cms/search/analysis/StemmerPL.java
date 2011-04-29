package net.cyklotron.cms.search.analysis;

/**
 * Stemmer for polish language using <a href="http://www.getopt.org/stempel/">Stempel</a>
 * 
 * @author rafal
 */
public class StemmerPL
    implements Stemmer
{
    private final static String STEM_TABLE = "/org/getopt/stempel/tables/stemmer_2000.out";

    private final org.getopt.stempel.Stemmer stemmerImpl = new org.getopt.stempel.Stemmer(
        STEM_TABLE);

    @Override
    public String stem(String term)
    {
        return stemmerImpl.stem(term, true);
    }
}
