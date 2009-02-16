package net.cyklotron.cms.syndication.internal;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.cyklotron.cms.syndication.IncomingFeedContentProcessor;

/**
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: XSLIncomingFeedContentProcessor.java,v 1.1 2005-06-16 11:14:21 zwierzem Exp $
 */
public class XSLIncomingFeedContentProcessor implements IncomingFeedContentProcessor
{
    private String contents;
    private Source templateSource;

    public XSLIncomingFeedContentProcessor(String contents, InputStream templateIS)
    {
        this.templateSource = new StreamSource(templateIS);
        this.contents = contents;
    }

    public String process() throws Exception
    {
        // prepare a transformer
        // the two followin lines may be moved to XSLTemplateManager and allow templates
        // object to be cached
        TransformerFactory xformFactory = TransformerFactory.newInstance();
        Templates templates = xformFactory.newTemplates(templateSource);
        // catch(TransformerConfigurationException e)
        
        Transformer transformer = templates.newTransformer();
        // catch(TransformerConfigurationException e)
        
        StreamSource source = new StreamSource(new StringReader(contents));
        StringWriter writer = new StringWriter(contents.length());
        StreamResult scrResult = new StreamResult(writer);

        transformer.transform(source, scrResult);
        // catch(TransformerException e)

        return writer.toString();
    }
}
