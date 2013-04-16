/**
 * 
 */
package net.cyklotron.cms.search.analysis;

import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.pattern.PatternReplaceFilter;

/**
 * A token filter that retains only characters that belong to Unicode L and N classes. This filter
 * is useful for stripping quotes, parens and punctuation from token stream.
 * 
 * @author rafal, marek
 */
public class AlphanumericFilter
    extends TokenFilter
{
    private static final String NON_ALPHANUMERIC = "[^\\p{L}\\p{N}]";

    private static final Pattern pattern = Pattern.compile(NON_ALPHANUMERIC);

    private final PatternReplaceFilter patternReplaceFilter;

    private static final boolean REPLACE_ALL = true;

    public AlphanumericFilter(TokenStream input)
    {
        super(input);
        patternReplaceFilter = new PatternReplaceFilter(input, pattern, "", REPLACE_ALL);
    }

    @Override
    public boolean incrementToken()
        throws IOException
    {
        return patternReplaceFilter.incrementToken();
    }
}