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
        extends Tokenizer
    {
        private final TermAttribute termAttribute = addAttribute(TermAttribute.class);

        private final OffsetAttribute offsetAttribute = addAttribute(OffsetAttribute.class);

        private LineNumberReader lineReader;

        private int lastTokenStart = 0;

        public NewlineSeparatedTokenizer(String fieldName, Reader reader)
        {
            reset(reader);
        }

        /**
         * Produces next token in the stream.
         */
        @Override
        public boolean incrementToken()
            throws IOException
        {
            String line = lineReader.readLine();
            if(line != null)
            {
                termAttribute.setTermBuffer(line);
                int tokenStart = lastTokenStart;
                int tokenEnd = lastTokenStart + line.length();
                lastTokenStart = tokenEnd + 1;
                offsetAttribute.setOffset(tokenStart, tokenEnd);
                return true;
            }
            else
            {
                return false;
            }
        }

        /** Releases resources associated with this stream. */
        @Override
        public void close()
            throws IOException
        {
            lineReader.close();
        }

        /** Reset the tokenizer to a new reader. */
        @Override
        public void reset(Reader reader)
        {
            lineReader = new LineNumberReader(reader);
            lastTokenStart = 0;
        }
    }
}
