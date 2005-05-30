package net.cyklotron.cms.documents.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.objectledge.ComponentInitializationError;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.ModificationNotPermitedException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;
import org.objectledge.encodings.HTMLEntityDecoder;
import org.picocontainer.Startable;

import pl.caltha.forms.ConstructionException;
import pl.caltha.forms.Form;
import pl.caltha.forms.FormsException;
import pl.caltha.forms.FormsService;

import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.documents.DocumentService;

/** Implementation of the DocumentService.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: DocumentServiceImpl.java,v 1.11 2005-05-30 00:16:22 zwierzem Exp $
 */
public class DocumentServiceImpl
    implements DocumentService, Startable
{
    private Logger log;

    private HTMLEntityDecoder entityDecoder = new HTMLEntityDecoder();

    /** Document edit form. */
    private Form form;
    
    private FormsService formsService;
    
    private CoralSessionFactory sessionFactory;

    // Need to create a way to diffetrentiate rml attributes from logical
    //       attributes stored as parts of RML attributes

    /** attribute definitions for rml attributes */
    private List attributeDefinitions = new ArrayList(16);
    /** compiled mapping xpaths for attribute values */
    private HashMap attributeXPaths = new HashMap();
    /** compiled mapping xpaths for DOM4J document */
    private HashMap dom4jdocXPaths = new HashMap();

    private HashMap attrMap;
    private HashMap domDocMap;

    /** Performs document service initialisationin in a following order:
     *  <ul>
     *      <li>document edit form initalisation</li>
     *      <li>document resource &lt;-&gt; document editing/viewing instance mapping
                initialisation</li>
     *      <li></li>
     *  </ul>
     */
    public DocumentServiceImpl(Configuration config, Logger logger, 
        FormsService formsService, CoralSessionFactory sessionFactory)
        throws ComponentInitializationError, ConfigurationException
    {
        System.out.println("DOC SERVICE STARTED!");
        this.log = logger;
        this.sessionFactory = sessionFactory;

        // I. document edit form initalisation
        this.formsService = formsService;
        String docEditFormURI = config.getChild("document.edit.form.definition.uri").getValue(null);
        if(docEditFormURI == null)
        {
            throw new ComponentInitializationError("Document edit form definition URI is not defined.");
        }
        try
        {
            form = formsService.getForm(docEditFormURI, DocumentService.FORM_NAME);
        }
        catch(ConstructionException e)
        {
            throw new ComponentInitializationError("Cannot build a document edit form", e);
        }
        catch(FormsException e)
        {
            throw new ComponentInitializationError("Cannot get a form definition", e);
        }

        // II. document resource <-> document editing/viewing instance mapping initialisation
        
        attrMap = new HashMap();
        domDocMap = new HashMap();
        Configuration[] cDefs = config.getChildren("attributeDefinition");
        for(int i = 0; i < cDefs.length;i++)
        {
            String name = cDefs[i].getAttribute("name");
            String attrName = cDefs[i].getAttribute("xPathAttribute");
            String domDoc = cDefs[i].getAttribute("xPathDomDoc");
            attrMap.put(name, attrName);
            domDocMap.put(name, domDoc);
        }
    }

    
    public void start()
    {
        ResourceClass documentResClass = null;
        CoralSession coralSession = sessionFactory.getRootSession();
        try
        {
            documentResClass =
                coralSession.getSchema().getResourceClass(DocumentNodeResource.CLASS_NAME);
            
            AttributeDefinition[] attrDefs = documentResClass.getAllAttributes();
            for(int i=0; i<attrDefs.length; i++)
            {
                String name = attrDefs[i].getName();
                String attributeXP = (String)attrMap.get(name);
                String dom4jdocXP = (String)domDocMap.get(name);

                if(attributeXP != null && dom4jdocXP != null)
                {
                    if(!attributeXP.equals("text()"))
                    {
                        attributeXPaths.put(name, new MyXPath(name, attributeXP));
                    }
                    dom4jdocXPaths.put(name, new MyXPath(name, dom4jdocXP));

                    attributeDefinitions.add(attrDefs[i]);
                }
            }

            // WARN: register listeners for cleaning up document cache
            coralSession.getEvent().addResourceChangeListener(this, documentResClass);
        }
        catch(EntityDoesNotExistException e)
        {
            log.error("Could not get document resource class", e);
        }
        finally
        {
            coralSession.close();
        }

    }

    public void stop()
    {
        
    }

    
    // net.cyklotron.cms.documents.DocumentService methods /////////////////////////////////////////

    /** Returns a form tool service's form definition object for editing CMS documents. */
    public Form getDocumentEditForm()
    {
        return form;
    }

    // document node <-> DOM4J doc conversion methods //////////////////////////////////////////////
    
    /** Copies the contents of a dom4j Document object into a given DocumentNodeResource. */
    public void copyToDocumentNode(Resource doc, org.dom4j.Document srcDoc) throws net.cyklotron.cms.documents.DocumentException
    {
        for(int i=0; i<attributeDefinitions.size(); i++)
        {
            AttributeDefinition attrDef = (AttributeDefinition)(attributeDefinitions.get(i));
			String name = attrDef.getName();
            MyXPath dom4jxPath = (MyXPath)dom4jdocXPaths.get(name);

			Object value = doc.get(attrDef);
            String attributeValue = getValueAsString(attrDef, value);

            MyXPath attributexPath = (MyXPath)attributeXPaths.get(name);
            if(attributexPath != null)
            {
                // xml attribute

                if(attributeValue != null && attributeValue.length() > 0)
                {
                    // parse the attributeValue
                    org.dom4j.Document attributeDoc = null;
                    try
                    {
                        attributeDoc = DocumentHelper.parseText(attributeValue);
                    }
                    catch(DocumentException e)
                    {
                        throw new net.cyklotron.cms.documents.DocumentException(
                                           "The XML value for field '" + name + "' is invalid", e);
                    }

                    // get dom subtree with dom4jXPath
                    Node fragment = dom4jxPath.getFragmentClone(srcDoc);

                    // paste the subtree to attribute tree
                    attributexPath.setFragment(attributeDoc, fragment);

                    // than getXML on attribute document
                    attributeValue = attributeDoc.getRootElement().asXML();
                }
                else
                {
                    attributeValue = dom4jxPath.getText(srcDoc, true);
                }
            }
            else
            {
                // plain attribute
                attributeValue = dom4jxPath.getText(srcDoc, false);
            }

            try
            {
				Object newValue = getStringAsValue(attrDef, attributeValue);
                if(newValue == null)
                {
                    //throw new net.cyklotron.cms.documents.DocumentException("Value of '"+name+"' attribute is null");
                    doc.unset(attrDef);                    
                }
                else
                {
                    doc.set(attrDef, newValue);
                }
            }
            catch(ModificationNotPermitedException e)
            {
                throw new net.cyklotron.cms.documents.DocumentException("No permission to set '"+name+"' attribute", e);
            }
            catch(ValueRequiredException e)
            {
                throw new net.cyklotron.cms.documents.DocumentException("Value of '"+name+"' attribute is required", e);
            }
        }
    }

    /** Copies the contents of a DocumentNodeResource into a given dom4j Document object. */
    public void copyFromDocumentNode(Resource doc, org.dom4j.Document destDoc) throws net.cyklotron.cms.documents.DocumentException
    {
        
		for(int i=0; i<attributeDefinitions.size(); i++)
		{
			AttributeDefinition attrDef = (AttributeDefinition)(attributeDefinitions.get(i));
			String name = attrDef.getName();
            MyXPath dom4jxPath = (MyXPath)dom4jdocXPaths.get(name);

			Object value = doc.get(attrDef);
			String attributeValue = getValueAsString(attrDef, value);

			if(attributeValue == null || attributeValue.length() == 0)
			{
				// do nothing - rely on default value in target dom4j document
			}
			else
			{
				MyXPath attributexPath = (MyXPath)attributeXPaths.get(name);
	            if(attributexPath != null)
	            {
                    // xml attribute
                    attributeValue = entityDecoder.decodeXML(attributeValue);

                    // parse the attributeValue
                    org.dom4j.Document attributeDoc = null;
                    try
                    {
                        attributeDoc = DocumentHelper.parseText(attributeValue);
                    }
                    catch(DocumentException e)
                    {
                        throw new net.cyklotron.cms.documents.DocumentException(
                                "The XML value for field '" + name + "' is invalid", e);
                    }

                    // get attribute tree fragment
                    Node fragment = attributexPath.getFragment(attributeDoc);

                    // paste the attribute doc fragment to domdoc tree
                    dom4jxPath.setFragment(destDoc, fragment);
	            }
	            else
	            {
                    // non xml attribute
                    attributeValue = entityDecoder.decode(attributeValue);
	                dom4jxPath.setText(destDoc, attributeValue, false);
	            }
			}
        }
    }

	// implementation ////////////////////////////////////////////////////////////////////////////

	protected String getValueAsString(AttributeDefinition attrDef, Object value)
	{
		if(value == null)
		{
			return null;
		}
		
		Class clazz = attrDef.getAttributeClass().getJavaClass();
		String attributeValue = null;
		if(clazz == String.class)
		{
			attributeValue = (String)value;
		}
		else
		if(clazz == Date.class)
		{
			attributeValue = Long.toString( ((Date)value).getTime() );
		}
		else
		if(clazz == Integer.class)
		{
			attributeValue = ((Integer)value).toString();
		}
		else
		if(clazz == Long.class)
		{
			attributeValue = ((Long)value).toString();
		}
		/*else
		if(value instanceof DateRange)
		{
			attributeValue = ((DateRange)value).;
		}*/
		return attributeValue;
	}	

	protected Object getStringAsValue(AttributeDefinition attrDef, String attributeValue)
	{
		Class clazz = attrDef.getAttributeClass().getJavaClass();
		Object newValue = null;
		if(clazz == String.class)
		{
			newValue = attributeValue;
		}
		else
		if(clazz == Date.class)
		{
            if(attributeValue != null && !attributeValue.equals("")
               && !attributeValue.endsWith("/disabled"))
            {
                newValue = new Date(Long.parseLong(attributeValue));
            }
            else
            {
                newValue = null;
            }
		}
		else
		if(clazz == Integer.class)
		{
			newValue = new Integer(Integer.parseInt(attributeValue));
		}
		else
		if(clazz == Long.class)
		{
			newValue = new Long(Long.parseLong(attributeValue));
		}
		/*else
		if(clazz == DateRange.class)
		{
			newValue = new DateRange((Long.parseLong(attributeValue));
		}*/
		return newValue;
	}	

    // listener ///////////////////////////////////////////////////////////////////////////////////
    
    public void resourceChanged(Resource resource, Subject subject)
    {
        ((DocumentNodeResource)resource).clearCache();
    }
}