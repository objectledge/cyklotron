package net.cyklotron.cms.documents;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.dom4j.CDATA;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.dom.DOMDocument;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.objectledge.html.HTMLException;

/**
 * A few static methods for manipulating document metadata as Dom4j document.
 * 
 * @author rafal
 */
public class DocumentMetadataHelper
{
    /** The singleton instance of Dom4j DocumentFactory */
    private final static DocumentFactory FACTORY = DocumentFactory.getInstance();

    /**
     * Parse metadata into Dom4j document.
     * 
     * @param meta document metadata as text.
     * @return document metadata as Dom4j tree.
     * @throws HTMLException
     */
    public static Document textToDom4j(String meta)
        throws HTMLException
    {
        if(meta != null && meta.trim().length() > 0)
        {
            try
            {
                return DocumentHelper.parseText(meta);
            }
            catch(org.dom4j.DocumentException e)
            {
                throw new HTMLException("document metadata contains invalid XML", e);
            }
        }
        else
        {
            return new DOMDocument();
        }
    }
    
    /**
     * Serialize metadata into text.
     * 
     * @param doc document metadata as Dom4j tree.
     * @return document metadata as text.
     */
    public static String dom4jToText(Document doc)
    {
        StringWriter sw = new StringWriter();
        OutputFormat of = new OutputFormat();
        of.setSuppressDeclaration(true);
        XMLWriter xw = new XMLWriter(sw, of);
        try
        {
            xw.write(doc);
            return sw.toString();
        }
        catch(IOException e)
        {
            throw new RuntimeException("unexpected", e);
        }
    }

    /**
     * Collect all text from selected document nodes selected by an XPath expression.
     * 
     * @param metaDoc document metadata as Dom4j tree.
     * @param xpath XPath expression.
     * @return collected text.
     */
    @SuppressWarnings("unchecked")
    public static String selectAllText(Document metaDoc, String xpath)
    {
        StringBuilder buf = new StringBuilder(256);
        collectText((List<Element>)metaDoc.selectNodes(xpath), buf);
        return buf.toString().trim();
    }

    /**
     * Recursively collect text from a list of Dom4j elements.
     * 
     * @param elements elements.
     * @param buff output buffer.
     */
    @SuppressWarnings("unchecked")
    private static void collectText(List<Element> elements, StringBuilder buff)
    {
        for(Element e : elements)
        {
            buff.append(e.getTextTrim()).append(' ');
            collectText((List<Element>)e.elements(), buff);
        }
    }

    /**
     * Return value of the first text node selected by an XPath expression.
     * 
     * @param metaDoc document metadata as Dom4j tree.
     * @param xpath XPath expression.
     * @return selected text.
     */
    @SuppressWarnings("unchecked")
    public static String selectFirstText(Document metaDoc, String xpath)
    {
        List<Element> elements = (List<Element>)metaDoc.selectNodes(xpath);
        if(elements.size() == 0)
        {
            return "";
        }
        else
        {
            return elements.get(0).getTextTrim();
        }
    }
    
    /**
     * Creates a Dom4J Element with specified name.
     * 
     * @param name of the element.
     * @return an element.
     */
    public static Element elm(String name)
    {
        return FACTORY.createElement(name);
    }

    /**
     * Creates a Dom4J Element with specified name and text contents.
     * 
     * @param name of the element.
     * @param text the text content of the element.
     * @return an element.
     */
    public static Element elm(String name, String text)
    {
        if(text == null || text.length() == 0)
        {
            return elm(name);            
        }
        else
        {
            return FACTORY.createElement(name).addText(text);            
        }
    }
    
    /**
     * Creates a Dom4J CDATA node with specified text contents.
     * 
     * @param text the text content of the CDATA node.
     * @return an CDATA node.
     */
    public static CDATA cdata(String text)
    {
        return FACTORY.createCDATA(text);
    }
    
    /**
     * Creates a Dom4j Element with specified name and contents.
     *  
     * @param name of the element.
     * @param elmements the contents of the element.
     * @return an element.
     */
    public static Element elm(String name, Node ... elements)
    {
        Element parent = elm(name);
        for(Node child : elements)
        {
            parent.add(child);
        }
        return parent;
    }
    
    /**
     * Creates a Dom4j Document this specified root Element.
     * 
     * @param rootElement the root Element.
     * @return a Document.
     */
    public static Document doc(Element rootElement)
    {
        return FACTORY.createDocument(rootElement);
    }
}
