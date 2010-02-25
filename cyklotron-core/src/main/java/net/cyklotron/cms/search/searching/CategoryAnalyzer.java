package net.cyklotron.cms.search.searching;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.Set;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.util.Version;

import net.cyklotron.cms.search.SearchConstants;

/**
 * 
 * @author <a href="mailto:damian@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryAnalyzer.java,v 1.2 2005-01-27 02:11:54 pablo Exp $
 */
public class CategoryAnalyzer extends StandardAnalyzer implements SearchConstants {
    
    /** Builds an analyzer. */
    public CategoryAnalyzer() 
    {
        super(Version.LUCENE_30);
    }

    /** Builds an analyzer with the given stop words. */
    public CategoryAnalyzer(Set stopWords)
    {
        super(Version.LUCENE_30, stopWords);
    }	

    @Override
    public TokenStream tokenStream(String fieldName, Reader reader) 
    {
    	if(fieldName.equals(FIELD_CATEGORY))
    	{
			return new CategoryTokenStream(fieldName, reader);
    	}	
    	else
    	{
			return super.tokenStream(fieldName, reader);
    	}
    }
    
    
    public class CategoryTokenStream
    	extends TokenStream
    {
    	private final LineNumberReader lineReader;
    	private final TermAttribute termAttribute = addAttribute(TermAttribute.class);
    	private final OffsetAttribute offsetAttribute = addAttribute(OffsetAttribute.class);
    	
    	private int lastTokenStart = 0;
    	
		public CategoryTokenStream(String fieldName, Reader reader)
    	{
    		lineReader = new LineNumberReader(reader);
	   	} 
		
		/**
		 * Produces next token in the stream.
		 */
		@Override
        public boolean incrementToken() throws IOException
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
        public void close() throws IOException
		{
			lineReader.close();
		}
    }
}
