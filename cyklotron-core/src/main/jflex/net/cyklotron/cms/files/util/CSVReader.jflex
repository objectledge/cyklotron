package net.cyklotron.files.util;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.jcontainer.dna.Logger;

/**
 * RFC 4180 compliant CSV parser.
 */
%%

%public
%class CSVReader
%unicode
%integer
%apiprivate
%implements AutoCloseable
%ctorarg char fieldSeparator

%state QUOTED, AFTER_FIELD, AFTER_RECORD

%init{
	this.fieldSeparator = fieldSeparator;
%init}

%{
	private char fieldSeparator = ',';
	private int line = 1;
	private int column = 1;
    private int fieldStart = 1;
	private StringBuffer curChars = new StringBuffer();
	private List<String> curLine;
	
	private List<String> headers;
	
	private Logger log = null;
	
	public boolean atEOF()
	{
		return zzAtEOF;
	}
	
	/**
	 * Reads next record from input. At end of input, an empty list will be returned.
	 */
	public List<String> readRecord()
		throws IOException
	{
		curLine = new ArrayList<String>();
		int state;
		do 
		{
		    state = yylex();
		}
		while(state != AFTER_RECORD && state != YYEOF);
		return curLine;
	}
	
	/**
	 * Reads all non-empty records from input.
	 */
	public List<List<String>> readData()
		throws IOException
	{
		List<List<String>> data = new ArrayList<List<String>>();
		List<String> record;
		while(!zzAtEOF)
		{
			record = readRecord();
			if(record.size() > 0)
			{
				data.add(record);
			}
		}
		return data;
	}
	
	/**
	 * Reads a record from input to be used as column headers for subsequent invocations of {@link #readMappedRecord()} 
	 * and {@link #readMappedData}
	 */
	public List<String> readHeaders()
		throws IOException
	{
		headers = readRecord();
		return headers;
	}
	
	/**
	 * Reads next record from input an transforms it into a map keyed with values of column headers fetched 
	 * with {@link #readHeaders}.
	 * @throws IllegalStateException when called without prior invocation of {@link #readHeaders()}
	 */
	public Map<String, String> readMappedRecord()
		throws IOException
	{
		if(headers != null)
		{
			List<String> record = readRecord();
			Map<String, String> mapped = new HashMap<String, String>();
			for(int i = 0; i < Math.min(record.size(), headers.size()); i++)
			{
				mapped.put(headers.get(i), record.get(i));
			}
			return mapped;
		}	
		else
		{
			throw new IllegalStateException("readHeaders() has not been called yet");
		}
	}

	/**
	 * Reads all non-empty records from input an transforms them into a maps keyed with values of 
	 * column headers fetched with {@link #readHeaders}.
	 * @throws IllegalStateException when called without prior invocation of {@link #readHeaders()}
	 */
	public List<Map<String, String>> readMappedData()
		throws IOException
	{
		if(headers != null)
		{
			List<Map<String, String>> data = new ArrayList<Map<String, String>>();
			Map<String, String> record;
			while(!zzAtEOF)
			{
				record = readMappedRecord();
				if(!record.isEmpty())
				{
					data.add(record);
				}
			}
			return data;
		}	
		else
		{
			throw new IllegalStateException("readHeaders() has not been called yet");
		}
	}
	
	public void close()
		throws IOException
	{
		yyclose();
	}
	
	private String printSep(char sep)
	{
		if(sep == '\t')
		{
			return "\\t";
		}
		else
		{
			return new String(new char[] { sep });
		}
	}
	
	public void setLog(Logger log)
	{
		this.log = log;
	}
	
	private void log(String message)
	{
	    if(log != null)
	    {
	    	log.debug(message);
	    }
	}
%}

eol = (\n|\r\n) 
term = ([;,\t])

%%

<YYINITIAL, AFTER_FIELD, AFTER_RECORD> 
{
    [^\";,\t\r\n]+ 
    { 
        log("field content at " + line + ":" + column + " lenght " + yylength()); 
        curChars.append(yytext()); 
        fieldStart = column;
        column += yylength();
    }
    
    {term}
    {
    	if(yycharat(0) == fieldSeparator)
    	{
    	   log("field terminator at " + line + ":" + column);
           curLine.add(curChars.toString()); 
           curChars.setLength(0);
           column++;
    	   return AFTER_FIELD;
    	}
    	else
    	{
    	   log("non-terminator " + printSep(yycharat(0)) + " at " + line + ":" + column);
    	   curChars.append(yycharat(0));
    	   column++;
    	}
    }
 
    "\"\""
    {
    	log("escaped quote at " + line + ":" + column);
    	curChars.append("\"");
    	column += 2; 
    }
    
    "\""
    {
    	log("quoted field start at " + line + ":" + column);
        fieldStart = column;
        yybegin(QUOTED);
        column++;
    }
        
    {eol}  
    { 
        log("end of line " + line);
        if(curChars.length() > 0)
        {
        	curLine.add(curChars.toString());
        	curChars.setLength(0);
        }
        line++; 
        column = 1;        
        return AFTER_RECORD; 
    }
}

<QUOTED>
{
    [^\"]+
    { 
        log("quoted text at " + line + ":" + column + " length " + yylength()); 
        curChars.append(yytext());
        column += yylength(); 
    }
	
	"\"\""
	{
	    log("escaped quote at " + line + ":" + column);
	    curChars.append('"');
	    column += 2;
	}
	
	"\""
	{
	    log("quoted text end at " + line + ":" + column);
	    column++;
	    yybegin(AFTER_FIELD);
	}
}

<YYINITIAL> <<EOF>>
{
    log("end of line " + line + " at EOF");
    if(curChars.length() > 0)
    {
         curLine.add(curChars.toString());
    }
    return YYEOF;
}

<QUOTED> <<EOF>> 
{ 
    throw new Error("non matched quote at " + line + ":" + fieldStart);
}
