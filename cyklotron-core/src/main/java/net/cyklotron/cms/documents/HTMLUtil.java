package net.cyklotron.cms.documents;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * Utility functions for HTML manipulation.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: HTMLUtil.java,v 1.3 2005-05-30 00:06:28 zwierzem Exp $
 */
public class HTMLUtil
{
    public static Document parseXmlAttribute(String value, String attributeName)
    throws net.cyklotron.cms.documents.DocumentException
    {
        // parse a document fragment
        Document fragment = null;
        try
        {
            fragment = DocumentHelper.parseText(value);
        }
        catch(org.dom4j.DocumentException e)
        {
            throw new net.cyklotron.cms.documents.DocumentException(
                "The XML value for attribute '"+attributeName+"' is invalid", e);
        }
        return fragment;
    }

    public static Document emptyHtmlDom()
    {
        DocumentFactory factory = DocumentFactory.getInstance();
        Document document = factory.createDocument();
        Element html = document.addElement("html");
        Element head = html.addElement("head").addElement("title");
        Element body = html.addElement("body");
        return document;
    }

    /** Removes everything but <code>&lt;body&gt;</code> tag contents.
     *  This one is stupid and assumes that there is no > cahractr in any of body
     *  tags attribute values.
     */
    public static String stripHTMLHead(String htmlDoc)
    {
        int bodyStartIndex = htmlDoc.indexOf("<body");
        int bodyEndIndex = htmlDoc.indexOf("</body>");

        if(bodyStartIndex > -1)
        {
            for(int i = bodyStartIndex; i < bodyEndIndex; i++)
            {
                if(htmlDoc.charAt(i) == '>')
                {
                    bodyStartIndex = i+1;
                    break;
                }
            }

            if(bodyStartIndex < bodyEndIndex)
            {
                return htmlDoc.substring(bodyStartIndex, bodyEndIndex);
            }
        }
        
        return htmlDoc;
    }

    @SuppressWarnings("unchecked")
    public static String getFirstText(Document metaDom, String xpath)
    {
        List<Element> elements = (List<Element>)metaDom.selectNodes(xpath);
        if(elements.size() == 0)
        {
            return "";
        }
        else
        {
            return elements.get(0).getTextTrim();
        }
    }

    public static String getAllText(org.dom4j.Document metaDom, String xpath)
    {
        StringBuilder buf = new StringBuilder(256);
        HTMLUtil.collectText((List<Element>)metaDom.selectNodes(xpath), buf);
        return buf.toString().trim();        
    }

    private static void collectText(List<Element> elements, StringBuilder buff)  
    {
        for(Element e : elements)
        {
            buff.append(e.getTextTrim()).append(' ');
            collectText((List<Element>)e.elements(), buff);
        }
    }
}

