package net.cyklotron.cms.documents.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import org.dom4j.io.DOMReader;
import org.dom4j.io.OutputFormat;
import org.jcontainer.dna.Logger;
import org.objectledge.coral.Instantiator;
import org.objectledge.encodings.HTMLEntityEncoder;
import org.w3c.dom.Document;
import org.w3c.tidy.Configuration;
import org.w3c.tidy.Tidy;

import pl.caltha.forms.internal.util.TidyWrapper;

import net.cyklotron.cms.documents.HTMLException;
import net.cyklotron.cms.documents.HTMLService;
import net.cyklotron.cms.documents.HTMLTextCollectorVisitor;
import net.cyklotron.cms.documents.HTMLUtil;

/** Implementation of the DocumentService.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: HTMLServiceImpl.java,v 1.7 2005-12-30 11:00:52 rafal Exp $
 */
public class HTMLServiceImpl
	implements HTMLService
{
    private Logger log;

    private Instantiator instantiator;
    

    public HTMLServiceImpl(Logger logger, Instantiator instantiator)
    {
        log = logger;
        this.instantiator = instantiator;
        //poolService = (PoolService)(broker.getService(PoolService.SERVICE_NAME));
    }

    // net.cyklotron.cms.documents.HTMLService methods /////////////////////////////////////////

	public String encodeHTML(String html, String encodingName)
        throws Exception
	{ 
		String encodedHtml = "";
		if(html != null && html.length() > 0)
		{
			HTMLEntityEncoder encoder =
				(HTMLEntityEncoder)instantiator.newInstance(HTMLEntityEncoder.class);
			encodedHtml = encoder.encodeHTML(html, encodingName);
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
        TidyWrapper tidyWrap = null;
        try
        {
            tidyWrap = (TidyWrapper)instantiator.newInstance(TidyWrapper.class);
        }
        catch(Exception e)
        {
            throw new HTMLException("Problems parsing HTML document", e);
        }

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
        // hack: decode &apos; 
        html = html.replace("&apos","'");
        return html;
    }
}