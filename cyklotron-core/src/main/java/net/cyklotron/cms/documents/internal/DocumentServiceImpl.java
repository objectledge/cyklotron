package net.cyklotron.cms.documents.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.documents.DocumentService;
import net.labeo.services.BaseService;
import net.labeo.services.ConfigurationError;
import net.labeo.services.InitializationError;
import pl.caltha.encodings.HTMLEntityDecoder;
import pl.caltha.forms.ConstructionException;
import pl.caltha.forms.Form;
import pl.caltha.forms.FormsException;
import pl.caltha.forms.FormsService;
import net.labeo.services.logging.LoggingFacility;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.AttributeDefinition;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.ModificationNotPermitedException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.ResourceClass;
import net.labeo.services.resource.ResourceService;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.services.webcore.ApplicationService;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

/** Implementation of the DocumentService.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: DocumentServiceImpl.java,v 1.1 2005-01-12 20:44:27 pablo Exp $
 */
public class DocumentServiceImpl extends BaseService implements DocumentService
{
    private LoggingFacility log;

    private HTMLEntityDecoder entityDecoder = new HTMLEntityDecoder();

    /** Document edit form. */
    private Form form;

    // TODO: Need to create a way to diffetrentiate rml attributes from logical
    //       attributes stored as parts of RML attributes

    /** attribute definitions for rml attributes */
    private List attributeDefinitions = new ArrayList(16);
    /** compiled mapping xpaths for attribute values */
    private HashMap attributeXPaths = new HashMap();
    /** compiled mapping xpaths for DOM4J document */
    private HashMap dom4jdocXPaths = new HashMap();

    // net.labeo.services.Service methods //////////////////////////////////////////////////////////

    /** Performs document service initialisationin in a following order:
     *  <ul>
     *      <li>document edit form initalisation</li>
     *      <li>document resource &lt;-&gt; document editing/viewing instance mapping
                initialisation</li>
     *      <li></li>
     *  </ul>
     */
    public void start()
    {
        LoggingService logService = (LoggingService)
                        (broker.getService(LoggingService.SERVICE_NAME));
        log = logService.getFacility(LOGGING_FACILITY);

        // I. document edit form initalisation
        FormsService formService = (FormsService)broker
                                        .getService(FormsService.SERVICE_NAME);

        ApplicationService appService = (ApplicationService)broker
                                        .getService(ApplicationService.SERVICE_NAME);
        String docEditFormURI = appService.getConfiguration("cms")
                                        .get("document.edit.form.definition.uri").asString(null);

        if(docEditFormURI == null)
        {
            throw new ConfigurationError("Document edit form definition URI is not defined.");
        }

        try
        {
            form = formService.getForm(docEditFormURI, DocumentService.FORM_NAME);
        }
        catch(ConstructionException e)
        {
            throw new ConfigurationError("Cannot build a document edit form", e);
        }
        catch(FormsException e)
        {
            throw new ConfigurationError("Cannot get a form definition", e);
        }

        // II. document resource <-> document editing/viewing instance mapping initialisation

		ResourceService resourceService =
			(ResourceService)broker.getService(ResourceService.SERVICE_NAME);

        ResourceClass documentResClass = null;
        try
        {
            documentResClass =
            	resourceService.getSchema().getResourceClass(DocumentNodeResource.CLASS_NAME);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new InitializationError("Document resource class cannot be found", e);
        }
	
		AttributeDefinition[] attrDefs = documentResClass.getAllAttributes();
        for(int i=0; i<attrDefs.length; i++)
        {
            String name = attrDefs[i].getName();
			String attributeXP = config.get("xpath.attribute."+name).asString(null);
			String dom4jdocXP = config.get("xpath.domdoc."+name).asString(null);

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
        resourceService.getEvent().addResourceChangeListener(this, documentResClass);
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
            // TODO Only string attributes may be xml
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
				// do nothing - relay on default value in target dom4j document
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
		// TODO add support for other non string attributes (ie - daterange)
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