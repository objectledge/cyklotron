package net.cyklotron.cms.documents.internal;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.InvalidXPathException;
import org.dom4j.Node;
import org.dom4j.XPath;

import net.labeo.services.ConfigurationError;

/**
 * XPath expression class for getting and setting values in DOM4J documents.
 *
 * @author    <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version   $Id: MyXPath.java,v 1.1 2005-01-12 20:44:27 pablo Exp $
 */
public class MyXPath
{
    /** Name of this XPath expression. */
    private String name;
    /** Text representation of an expression. */
    private String xPathExp;
    /** The expression itself. */
    private XPath xPath;

    /**
     * Constructor for the MyXPath object
     *
     * @param name     name of this XPath expression
     * @param xPathExp the expression itself
     */
    public MyXPath(String name, String xPathExp)
        throws ConfigurationError
    {
        this.name = name;
        this.xPathExp = xPathExp;
        try
        {
            this.xPath = DocumentHelper.createXPath(xPathExp);
        }
        catch(InvalidXPathException e)
        {
            throw new ConfigurationError("Invalid XPath for attribute '"+name+"'='"+xPathExp+"'", e);
        }
    }


    /** Getter for property name.
     * @return Value of property name.
     *
     */
    public String getName()
    {
        return name;
    }


    /** Getter for property xPathExp.
     * @return Value of property xPathExp.
     *
     */
    public String getXPathExp()
    {
        return xPathExp;
    }
    
    
    /**
     * Stick a piece of text into the document (may be an XML fragment).
     *
     * @param document               Document to be modified.
     * @param value                  Value to be set in document.
     * @return                       Document node which was modified.
     * @exception DocumentException  Thrown on problems with set value and modified node.
     */
    public Node setText(Document document, String value, boolean valueAsXML)
        throws net.cyklotron.cms.documents.DocumentException
    {
        Node contextNode = getFragment(document);

        if(value == null)
        {
            value = "";
            //throw new net.cyklotron.cms.documents.DocumentException("Cannot set null values");
         }
        
        if(valueAsXML && !value.equals( "" ))
        {
            // parse a document fragment
            Document fragment = null;
            try
            {
                fragment = DocumentHelper.parseText(value);
            }
            catch(DocumentException e)
            {
                throw new net.cyklotron.cms.documents.DocumentException(
                                            "The XML value for field '"+name+"' is invalid", e);
            }
            // stick it into the document
            return setFragment(document, fragment);
        }
        else
        {
            contextNode.setText(value);
            return contextNode;
        }
    }


    /**
     * Retrieves a value from a given document.
     *
     * @param document               Document from which values is drawn.
     * @param valueAsXML             Determines if a values should be retrieved as a fragment of XML
     *                               markup.
     * @return                       String representation of a retrieved value (may an XML fragment).
     * @exception DocumentException  Thrown on problems with document node from which a value is
     *      retrieved.
     */
    public String getText(Document document, boolean valueAsXML)
        throws net.cyklotron.cms.documents.DocumentException
    {
        Node contextNode = getFragment(document);

        if(valueAsXML)
        {
            return contextNode.asXML();
        }
        else
        {
            // WARN: Ugly hack here - dom4j throws NullPointerException when
            // there is no text inside contextNode or it returns "" or null.
            try
            {
                String text = contextNode.getText();
                if(text == "")
                {
                    return null;
                }
                return text;
            }
            catch(NullPointerException e)
            {
                return null;
            }
        }
    }

    public Node setFragment(Document document, Node fragment)
        throws net.cyklotron.cms.documents.DocumentException
    {
        Node contextNode = getFragment(document);

        if(fragment != null)
        {
            Node valueNode = fragment;
            if(fragment instanceof Document)
            {
                valueNode = ((Document)fragment).getRootElement();
            }

             // if a contextNode is a document than replace the whole doc
            if(contextNode instanceof Document ||
               contextNode == document.getRootElement())
            {
                if(valueNode instanceof Element)
                {
                    document.setRootElement((Element)valueNode);
                }
                else
                {
                    throw new net.cyklotron.cms.documents.DocumentException(
                        "Cannot replace document root with a non element node.");
                }
            }
            else
            {
                // get a parent element of the context node
                Element contextNodeParent = contextNode.getParent();
                checkNode(contextNodeParent);

                // replace the context node with the fragment
                List elements = contextNodeParent.elements();
                int index = elements.indexOf(contextNode);
                elements.add(index, valueNode);
                contextNode.detach();
            }
        }
        return contextNode;
    }
    
    /**
     * Retrieves a document fragment from a given document.
     *
     * @exception DocumentException  Thrown on problems with document node from which a value is
     *      retrieved.
     */
    public Node getFragment(Document document)
        throws net.cyklotron.cms.documents.DocumentException
    {
        Node contextNode = xPath.selectSingleNode(document.getRootElement());
        checkNode(contextNode);

        return contextNode;
    }

    /**
     * Retrieves a document fragment clone from a given document.
     *
     * @param document               Document from which values is drawn.
     * @return                       A Document fragment
     * @exception DocumentException  Thrown on problems with document node from which a value is
     *      retrieved.
     */
    public Node getFragmentClone(Document document)
        throws net.cyklotron.cms.documents.DocumentException
    {
        return (Node)getFragment(document).clone();
    }

    /**
     * Checks if a node retrived with this XPath expression exist.
     *
     * @param node                   Node to be checked.
     * @exception DocumentException  Thrown when the checked node does not exist.
     */
    private void checkNode(Node node) throws net.cyklotron.cms.documents.DocumentException
    {
        if(node == null)
        {
            throw new net.cyklotron.cms.documents.DocumentException("The org.dom4j.Document provided to set/get the value for field '" + name
                     + "' is invalid for XPath expression '" + xPath.getText() + "'");
        }
    }  
}
