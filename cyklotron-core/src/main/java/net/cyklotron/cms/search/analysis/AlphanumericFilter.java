/**
 * 
 */
package net.cyklotron.cms.search.analysis;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

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
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);

    public AlphanumericFilter(TokenStream input)
    {
        super(input);
    }

    @Override
    public boolean incrementToken()
        throws IOException
    {
        if(!input.incrementToken())
        {
            return false;
        }
        final String replaced = termAtt.toString().replaceAll("[^\\p{L}\\p{N}]", "");
        // TODO validate that this is correct. I still don't have a feeling about those buffers
        termAtt.copyBuffer(replaced.toCharArray(), 0, replaced.length());
        return true;
    }
}