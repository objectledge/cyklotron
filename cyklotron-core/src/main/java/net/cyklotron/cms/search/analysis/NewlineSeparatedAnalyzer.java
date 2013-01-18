package net.cyklotron.cms.search.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.util.CharTokenizer;
import org.apache.lucene.util.Version;

public class NewlineSeparatedAnalyzer
    extends Analyzer
{
    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader)
    {
        return new TokenStreamComponents(new NewlineSeparatedTokenizer(reader));
    }
    
    // Tokenizer implementation ///////////////////////////////////////////////////////////////////

    public static class NewlineSeparatedTokenizer
        extends CharTokenizer
    {
        public NewlineSeparatedTokenizer(Reader input)
        {
            super(Version.LUCENE_40, input);
        }

        @Override
        protected boolean isTokenChar(int c)
        {
            return !isLineSeparator(c);
        }

        private static boolean isLineSeparator(int codePoint)
        {
            return (((1 << Character.LINE_SEPARATOR) >> Character.getType(codePoint)) & 1) != 0;
        }
    }
}