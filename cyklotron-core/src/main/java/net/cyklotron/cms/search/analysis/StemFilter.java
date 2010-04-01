package net.cyklotron.cms.search.analysis;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;

public class StemFilter
    extends TokenFilter
{
    private final Stemmer stemmer;

    private final TermAttribute termAtt;

    public StemFilter(TokenStream in, Stemmer stemmer)
    {
        super(in);
        this.stemmer = stemmer;
        termAtt = addAttribute(TermAttribute.class);
    }

    @Override
    public boolean incrementToken()
        throws IOException
    {
        if(input.incrementToken())
        {
            String term = termAtt.term();
            String s = stemmer.stem(term);
            // If not stemmed, don't waste the time adjusting the token.
            if((s != null) && !s.equals(term))
                termAtt.setTermBuffer(s);
            return true;
        }
        else
        {
            return false;
        }
    }
}
