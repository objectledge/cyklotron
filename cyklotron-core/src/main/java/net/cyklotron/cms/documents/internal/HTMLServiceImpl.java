package net.cyklotron.cms.documents.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import net.cyklotron.cms.documents.HTMLException;
import net.cyklotron.cms.documents.HTMLService;
import net.cyklotron.cms.documents.HTMLTextCollectorVisitor;
import net.cyklotron.cms.documents.HTMLUtil;
import pl.caltha.encodings.HTMLEntityEncoder;
import pl.caltha.forms.internal.util.TidyWrapper;
import net.labeo.services.BaseService;
import net.labeo.services.logging.Logger;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.pool.PoolService;

import org.dom4j.io.DOMReader;
import org.dom4j.io.OutputFormat;
import org.w3c.dom.Document;
import org.w3c.tidy.Configuration;
import org.w3c.tidy.Tidy;

/** Implementation of the DocumentService.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: HTMLServiceImpl.java,v 1.2 2005-01-18 17:38:21 pablo Exp $
 */
public class HTMLServiceImpl
	extends BaseService
	implements HTMLService
{
    private Logger log;

    /** pool service - for tidy objects */
    private PoolService poolService;
    
    // net.labeo.services.Service methods //////////////////////////////////////////////////////////

    public void init()
    {
        LoggingService logService = (LoggingService)broker.getService(LoggingService.SERVICE_NAME);
        log = logService.getFacility(LOGGING_FACILITY);
        poolService = (PoolService)(broker.getService(PoolService.SERVICE_NAME));
    }

    // net.cyklotron.cms.documents.HTMLService methods /////////////////////////////////////////

	public String encodeHTML(String html, String encodingName)
	{ 
		String encodedHtml = "";
		if(html != null && html.length() > 0)
		{
			HTMLEntityEncoder encoder =
				(HTMLEntityEncoder)(poolService.getInstance(HTMLEntityEncoder.class));
			encodedHtml = encoder.encodeHTML(html, encodingName);
			encoder.recycle();
		}
		return encodedHtml;
	}    

	public String htmlToText(String html)
	throws HTMLException
	{
		HTMLTextCollectorVisitor collector = new HTMLTextCollectorVisitor();
		parseHTML(html).accept(collector);
		return collector.getText();
	}

    public org.dom4j.Document parseHTML(String html)
	throws HTMLException
    {
        if(html == null || html.length() == 0)
        {
            throw new HTMLException("HTML document is empty");
        }
        
        org.dom4j.Document dom4jDoc = null;

        int bodyStartIndex = html.indexOf("<body");
        if(bodyStartIndex == -1)
        {
            html = "<html><head><title></title></head><body>"+html+"</body></html>";
        }

        // 1. parse the value using jTidy
		// 1.1. setup streams
		ByteArrayInputStream inputStream = null;
		ByteArrayOutputStream outputStream = null;
		try
		{
			inputStream = new ByteArrayInputStream(html.getBytes("UTF-8"));
			outputStream = new ByteArrayOutputStream(html.length()+256);
		}
		catch(UnsupportedEncodingException e)
		{
			// never happens
			throw new HTMLException("Problems parsing HTML document", e);
		}

        // 1.2. get tidy wrapper
        TidyWrapper tidyWrap = (TidyWrapper)(poolService.getInstance(TidyWrapper.class));

        // 1.3. setup tidy
        Tidy tidy = tidyWrap.getTidy();
        tidy.setTidyMark(false);
        tidy.setCharEncoding(Configuration.UTF8);
        tidy.setXHTML(true);
        tidy.setShowWarnings(false);
        tidy.setSpaces(0);
        tidy.setQuoteAmpersand(true);
        tidy.setQuoteNbsp(true);

        // 1.4. setup error information writer
        StringWriter errorWriter = new StringWriter(256);
        tidy.setErrout(new PrintWriter(errorWriter));

        // 1.5. run parse
        Document doc = tidy.parseDOM(inputStream, outputStream);

        // 2. check tidy if there were any errors.
        //    if there were no errors create a Dom4j document
        if(tidy.getParseErrors() == 0)
        {
            DOMReader domReader = new DOMReader();
            dom4jDoc = domReader.read(doc);
        }
        
        // return tidy wrapper to the pool
        tidyWrap.recycle();

        //check if anything bad happened
        if(dom4jDoc == null)
        {
            throw new HTMLException("Errors while parsing HTML document");
        }
        
        return dom4jDoc;
    }

    public String serializeHTML(org.dom4j.Document dom4jDoc)
    throws HTMLException
    {
        String html;
        
        StringWriter writer = new StringWriter(4096);
        OutputFormat format = new OutputFormat();
        format.setXHTML(true);
        format.setExpandEmptyElements(true);
        format.setTrimText(false);
        format.setIndent(false);
        MyHTMLWriter htmlWriter = new MyHTMLWriter(writer, format);
        try
        {
            htmlWriter.write(dom4jDoc);
            html = writer.toString();
        }
        catch(IOException e)
        {
            throw new HTMLException("Could not serialize the document", e);
        }

        // remove head
        html = HTMLUtil.stripHTMLHead(html);
        
        return html;
    }
}