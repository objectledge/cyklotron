package net.cyklotron.cms.documents;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.dom.DOMDocument;
import org.objectledge.html.HTMLException;

/**
 * A few static methods for manipulating document metadata as Dom4j document.
 * 
 * @author rafal
 */
public class DocumentMetadataHelper
{
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
}
