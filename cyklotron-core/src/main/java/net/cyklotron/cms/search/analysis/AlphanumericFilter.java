/**
 * 
 */
package net.cyklotron.cms.search.analysis;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;

/**
 * A token filter that retains only characters that belong to Unicode L and N classes. 
 *  
 *  This filter is useful for stripping quotes, parens and punctuation from token stream. 
 *  
 * @author rafal
 */
public class AlphanumericFilter
    extends TokenFilter
{
    private final TermAttribute termAtt;

    public AlphanumericFilter(TokenStream input)
    {
        super(input);
        termAtt = addAttribute(TermAttribute.class);
    }

    @Override
    public boolean incrementToken()
        throws IOException
    {
        if(!input.incrementToken())
        {
            return false;
        }
        termAtt.setTermBuffer(termAtt.term().replaceAll("[^\\p{L}\\p{N}]", ""));
        return true;
    }
}