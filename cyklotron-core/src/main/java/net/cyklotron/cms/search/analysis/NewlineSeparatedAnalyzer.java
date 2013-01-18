package net.cyklotron.cms.search.analysis;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;

public class NewlineSeparatedAnalyzer
    extends Analyzer
{
    @Override
    public TokenStream tokenStream(String fieldName, Reader reader)
    {
        return new NewlineSeparatedTokenizer(fieldName, reader);
    }

    @Override
    public TokenStream reusableTokenStream(String fieldName, Reader reader)
        throws IOException
    {
        Tokenizer tokenStream = (Tokenizer)getPreviousTokenStream();
        if(tokenStream == null)
        {
            tokenStream = new NewlineSeparatedTokenizer(fieldName, reader);
            setPreviousTokenStream(tokenStream);
        }
        else
        {
            tokenStream.reset(reader);
        }
        return tokenStream;
    }
    
    // Tokenizer implementation ///////////////////////////////////////////////////////////////////

    public static class NewlineSeparatedTokenizer
        extends CharTokenizer
    {

        public NewlineSeparatedTokenizer(Version matchVersion, Reader input)
        {
            super(matchVersion, input);
        }

        @Override
        protected boolean isTokenChar(int c)
        {
            return !isLineSeparator(c);
        }

    }

    public static boolean isLineSeparator(int codePoint)
    {
        return (((1 << Character.LINE_SEPARATOR) >> Character.getType(codePoint)) & 1) != 0;
    }
}
