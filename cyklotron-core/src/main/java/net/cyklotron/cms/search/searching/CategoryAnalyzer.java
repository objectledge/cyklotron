package net.cyklotron.cms.search.searching;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import net.cyklotron.cms.search.SearchConstants;

/**
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawe³ Potempski</a>
 * @version $Id: CategoryAnalyzer.java,v 1.1 2005-01-12 20:44:40 pablo Exp $
 */
public class CategoryAnalyzer extends StandardAnalyzer implements SearchConstants {
    
    /** Builds an analyzer. */
    public CategoryAnalyzer() 
    {
		super();
    }

    /** Builds an analyzer with the given stop words. */
    public CategoryAnalyzer(String[] stopWords) 
    {
		super(stopWords);
    }	

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
    	private LineNumberReader br;
    	
    	private String name;
    	
    	private int start;
    	
		public CategoryTokenStream(String fieldName, Reader reader)
    	{
    		br = new LineNumberReader(reader);
    		name = fieldName;
    		start = 0;
	   	} 
    	
		/** Returns the next token in the stream, or null at EOS. */
		public Token next() throws IOException
		{
			if(br.ready())
			{
				String line = br.readLine();
				if(line == null)
				{
					return null;
				}
				int localStart = start;
				int localEnd = start + line.length();
				start = localEnd + 1;														
				return new Token(line,localStart,localEnd);
			}
			return null;
		}

		/** Releases resources associated with this stream. */
		public void close() throws IOException
		{
			br.close();
		}
    }
}
