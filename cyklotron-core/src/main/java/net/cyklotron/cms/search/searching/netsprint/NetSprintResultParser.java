package net.cyklotron.cms.search.searching.netsprint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: NetSprintResultParser.java,v 1.1 2005-01-12 20:44:38 pablo Exp $
 */
public class NetSprintResultParser
extends DefaultHandler
implements ContentHandler, ErrorHandler
{
    private DateFormat format = new SimpleDateFormat("yyyy.MM.dd");

    private ResultsInfo info;
    private ArrayList results;

	private NetSprintSearchHit currentHit;
	private StringBuffer currentChars = new StringBuffer(256);
    private ArrayList stack = new ArrayList(5);

    // Implementation of ContentHandler interface. /////////////////////////////////////////////////
    
    /**
     * Receive notification of the start of an element.
     *
     * @param name The element type name.
     * @param attributes The specified or defaulted attributes.
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see org.xml.sax.ContentHandler#startElement
     */
    public void startElement (String uri, String localName, String qName, Attributes attributes)
	throws SAXException
    {
    	// clear chars buffer
    	currentChars.setLength(0);
    	
    	stack.add(localName);
    	
        try
        {
            if(localName.equals("info"))
            {
                //<info documents-found="(1)" query-time="(2)" pagenumber="(5)" has-previous="true" has-next="true"/>
                info = new ResultsInfo(attributes);
            }
            else
            if(localName.equals("result-list"))
            {
                results = new ArrayList(50);            
            }
            else
            if(localName.equals("result-row"))
            {
                //<result-row number="(6)" size="(10)" score="(11)" archive-id="(12)"  index-part="(13)" retrieval-date="(15)" is-intended="true" />
				currentHit = new NetSprintSearchHit(attributes, format);
            }
            else
            if(localName.equals("page-list"))
            {
                // do nothing
            }
            else
            if(localName.equals("page-link"))
            {
                //<page-link number="1" is-current="true"/>
            }
			if(localName.equals("search-results"))
			{
				// do nothing
			}
        }
        catch(Exception e)
        {
            throw new SAXException("problem parsing results attributes", e);
        }
    }
    
    
    /**
     * Receive notification of the end of an element.
     *
     * @param name The element type name.
     * @param attributes The specified or defaulted attributes.
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see org.xml.sax.ContentHandler#endElement
     */
    public void endElement (String uri, String localName, String qName)
	throws SAXException
    {
		stack.remove(stack.size()-1);

		String str = currentChars.toString();
		if(localName.equals("query"))
		{
			info.setQuery(str);
		}
		else
		if(localName.equals("old-query"))
		{
			info.setOldQuery(str);
		}
		else
		if(localName.equals("title"))
		{
			currentHit.setTitle(str);
		}
		else
		if(localName.equals("description"))
		{
			currentHit.setAbbreviation(str);
		}
		else
		if(localName.equals("url"))
		{
			currentHit.setUrl(str);
		}
		else
		if(localName.equals("others-from-domain"))
		{
			currentHit.set("others-from-domain", str);
		}
		else if(localName.equals("result-row"))
		{
			results.add(currentHit);
		}
    }

	public void characters(char[] ch, int start, int length) throws SAXException
	{
		currentChars.append(ch, start, length);
	}

    // Implementation of the ErrorHandler interface. ///////////////////////////////////////////////
    
    /**
     * Receive notification of a recoverable parser error.
     *
     * @param e The warning information encoded as an exception.
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see org.xml.sax.ErrorHandler#warning
     * @see org.xml.sax.SAXParseException
     */
    public void error (SAXParseException e)
	throws SAXException
    {
        throw e;
    }
    
    
    /**
     * Report a fatal XML parsing error.
     *
     * @param e The error information encoded as an exception.
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see org.xml.sax.ErrorHandler#fatalError
     * @see org.xml.sax.SAXParseException
     */
    public void fatalError(SAXParseException e)
	throws SAXException
    {
        throw e;
    }

    // Parser methods //////////////////////////////////////////////////////////////////////////////
    
    /** Getter for property info.
     * @return Value of property info.
     *
     */
    public ResultsInfo getInfo()
    {
        return info;
    }
    
    /** Getter for results.
     * @return an array containing results
     *
     */
    public NetSprintSearchHit[] getResults()
    {
        NetSprintSearchHit[] results2 = new NetSprintSearchHit[results.size()];
        results2 = (NetSprintSearchHit[])(results.toArray(results2));
        return results2;
    }
}
