//
// Copyright (c) 2012, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  All rights reserved. 
// 
package net.cyklotron.cms.docimport;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.objectledge.coral.BackendException;
import org.objectledge.coral.datatypes.ResourceList;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.ModificationNotPermitedException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;

import net.cyklotron.cms.CmsNodeResourceImpl;

/**
 * An implementation of <code>docimport.import</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class ImportResourceImpl
    extends CmsNodeResourceImpl
    implements ImportResource
{
    // class variables /////////////////////////////////////////////////////////

    /** Class variables initialization status. */
	@SuppressWarnings("unused")
    private static boolean definitionsInitialized;
	
    /** The AttributeDefinition object for the <code>abstractCleanupProfile</code> attribute. */
	private static AttributeDefinition<String> abstractCleanupProfileDef;

    /** The AttributeDefinition object for the <code>abstractEntityEncoded</code> attribute. */
    private static AttributeDefinition<Boolean> abstractEntityEncodedDef;

    /** The AttributeDefinition object for the <code>abstractXPath</code> attribute. */
	private static AttributeDefinition<String> abstractXPathDef;

    /** The AttributeDefinition object for the <code>attachentURLXPath</code> attribute. */
	private static AttributeDefinition<String> attachentURLXPathDef;

    /** The AttributeDefinition object for the <code>attachmentURLComposite</code> attribute. */
    private static AttributeDefinition<Boolean> attachmentURLCompositeDef;

    /** The AttributeDefinition object for the <code>attachmentURLSeparator</code> attribute. */
	private static AttributeDefinition<String> attachmentURLSeparatorDef;

    /** The AttributeDefinition object for the <code>attachmentsLocation</code> attribute. */
	private static AttributeDefinition<Resource> attachmentsLocationDef;

    /** The AttributeDefinition object for the <code>calendarStructureType</code> attribute. */
	private static AttributeDefinition<String> calendarStructureTypeDef;

    /** The AttributeDefinition object for the <code>categories</code> attribute. */
	private static AttributeDefinition<ResourceList> categoriesDef;

    /** The AttributeDefinition object for the <code>contentCleanupProfile</code> attribute. */
	private static AttributeDefinition<String> contentCleanupProfileDef;

    /** The AttributeDefinition object for the <code>contentEntitytEncoded</code> attribute. */
    private static AttributeDefinition<Boolean> contentEntitytEncodedDef;

    /** The AttributeDefinition object for the <code>contentXPath</code> attribute. */
	private static AttributeDefinition<String> contentXPathDef;

    /** The AttributeDefinition object for the <code>creationDateXPath</code> attribute. */
	private static AttributeDefinition<String> creationDateXPathDef;

    /** The AttributeDefinition object for the <code>dateFormat</code> attribute. */
	private static AttributeDefinition<String> dateFormatDef;

    /** The AttributeDefinition object for the <code>dateRangeEndParameter</code> attribute. */
	private static AttributeDefinition<String> dateRangeEndParameterDef;

    /** The AttributeDefinition object for the <code>dateRangeStartParameter</code> attribute. */
	private static AttributeDefinition<String> dateRangeStartParameterDef;

    /** The AttributeDefinition object for the <code>documentXPath</code> attribute. */
	private static AttributeDefinition<String> documentXPathDef;

    /** The AttributeDefinition object for the <code>footer</code> attribute. */
	private static AttributeDefinition<String> footerDef;

    /** The AttributeDefinition object for the <code>lastNewDocumentsCheck</code> attribute. */
	private static AttributeDefinition<Date> lastNewDocumentsCheckDef;

    /** The AttributeDefinition object for the <code>lastUpdatedDocumentsCheck</code> attribute. */
	private static AttributeDefinition<Date> lastUpdatedDocumentsCheckDef;

    /** The AttributeDefinition object for the <code>location</code> attribute. */
	private static AttributeDefinition<String> locationDef;

    /** The AttributeDefinition object for the <code>modificationDateXPath</code> attribute. */
	private static AttributeDefinition<String> modificationDateXPathDef;

    /** The AttributeDefinition object for the <code>originalURLXPath</code> attribute. */
	private static AttributeDefinition<String> originalURLXPathDef;

    /** The AttributeDefinition object for the <code>ownerLogin</code> attribute. */
	private static AttributeDefinition<String> ownerLoginDef;

    /** The AttributeDefinition object for the <code>sourceName</code> attribute. */
	private static AttributeDefinition<String> sourceNameDef;

    /** The AttributeDefinition object for the <code>targetLocation</code> attribute. */
	private static AttributeDefinition<Resource> targetLocationDef;

    /** The AttributeDefinition object for the <code>titleCleanupProfile</code> attribute. */
	private static AttributeDefinition<String> titleCleanupProfileDef;

    /** The AttributeDefinition object for the <code>titleEntityEncoded</code> attribute. */
    private static AttributeDefinition<Boolean> titleEntityEncodedDef;

    /** The AttributeDefinition object for the <code>titleXPath</code> attribute. */
	private static AttributeDefinition<String> titleXPathDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>docimport.import</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     */
    public ImportResourceImpl()
    {
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>docimport.import</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static ImportResource getImportResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof ImportResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not docimport.import");
        }
        return (ImportResource)res;
    }

    /**
     * Creates a new <code>docimport.import</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @param attachmentsLocation the attachmentsLocation attribute
     * @param location the location attribute
     * @param ownerLogin the ownerLogin attribute
     * @param sourceName the sourceName attribute
     * @param targetLocation the targetLocation attribute
     * @return a new ImportResource instance.
     * @throws ValueRequiredException if one of the required attribues is undefined.
     * @throws InvalidResourceNameException if the name argument contains illegal characters.
     */
    public static ImportResource createImportResource(CoralSession session, String name,
        Resource parent, Resource attachmentsLocation, String location, String ownerLogin, String
        sourceName, Resource targetLocation)
        throws ValueRequiredException, InvalidResourceNameException
    {
        try
        {
            ResourceClass<ImportResource> rc = session.getSchema().getResourceClass("docimport.import", ImportResource.class);
			Map<AttributeDefinition<?>, Object> attrs = new HashMap<AttributeDefinition<?>, Object>();
            attrs.put(rc.getAttribute("attachmentsLocation"), attachmentsLocation);
            attrs.put(rc.getAttribute("location"), location);
            attrs.put(rc.getAttribute("ownerLogin"), ownerLogin);
            attrs.put(rc.getAttribute("sourceName"), sourceName);
            attrs.put(rc.getAttribute("targetLocation"), targetLocation);
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof ImportResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (ImportResource)res;
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>abstractCleanupProfile</code> attribute.
     *
     * @return the value of the <code>abstractCleanupProfile</code> attribute.
     */
    public String getAbstractCleanupProfile()
    {
        return get(abstractCleanupProfileDef);
    }
    
    /**
     * Returns the value of the <code>abstractCleanupProfile</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>abstractCleanupProfile</code> attribute.
     */
    public String getAbstractCleanupProfile(String defaultValue)
    {
        return get(abstractCleanupProfileDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>abstractCleanupProfile</code> attribute.
     *
     * @param value the value of the <code>abstractCleanupProfile</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAbstractCleanupProfile(String value)
    {
        try
        {
            if(value != null)
            {
                set(abstractCleanupProfileDef, value);
            }
            else
            {
                unset(abstractCleanupProfileDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>abstractCleanupProfile</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>abstractCleanupProfile</code> attribute is defined.
	 */
    public boolean isAbstractCleanupProfileDefined()
	{
	    return isDefined(abstractCleanupProfileDef);
	}

    /**
     * Returns the value of the <code>abstractEntityEncoded</code> attribute.
     *
     * @return the value of the <code>abstractEntityEncoded</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getAbstractEntityEncoded()
        throws IllegalStateException
    {
	    Boolean value = get(abstractEntityEncodedDef);
        if(value != null)
        {
            return value.booleanValue();
        }
        else
        {
            throw new IllegalStateException("value of attribute abstractEntityEncoded is undefined"+
			    " for resource #"+getId());
        }
    }

    /**
     * Returns the value of the <code>abstractEntityEncoded</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>abstractEntityEncoded</code> attribute.
     */
    public boolean getAbstractEntityEncoded(boolean defaultValue)
    {
		return get(abstractEntityEncodedDef, Boolean.valueOf(defaultValue)).booleanValue();
	}

    /**
     * Sets the value of the <code>abstractEntityEncoded</code> attribute.
     *
     * @param value the value of the <code>abstractEntityEncoded</code> attribute.
     */
    public void setAbstractEntityEncoded(boolean value)
    {
        try
        {
            set(abstractEntityEncodedDef, Boolean.valueOf(value));
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
	
	/**
     * Removes the value of the <code>abstractEntityEncoded</code> attribute.
     */
    public void unsetAbstractEntityEncoded()
    {
        try
        {
            unset(abstractEntityEncodedDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>abstractEntityEncoded</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>abstractEntityEncoded</code> attribute is defined.
	 */
    public boolean isAbstractEntityEncodedDefined()
	{
	    return isDefined(abstractEntityEncodedDef);
	}
 
    /**
     * Returns the value of the <code>abstractXPath</code> attribute.
     *
     * @return the value of the <code>abstractXPath</code> attribute.
     */
    public String getAbstractXPath()
    {
        return get(abstractXPathDef);
    }
    
    /**
     * Returns the value of the <code>abstractXPath</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>abstractXPath</code> attribute.
     */
    public String getAbstractXPath(String defaultValue)
    {
        return get(abstractXPathDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>abstractXPath</code> attribute.
     *
     * @param value the value of the <code>abstractXPath</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAbstractXPath(String value)
    {
        try
        {
            if(value != null)
            {
                set(abstractXPathDef, value);
            }
            else
            {
                unset(abstractXPathDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>abstractXPath</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>abstractXPath</code> attribute is defined.
	 */
    public boolean isAbstractXPathDefined()
	{
	    return isDefined(abstractXPathDef);
	}
 
    /**
     * Returns the value of the <code>attachentURLXPath</code> attribute.
     *
     * @return the value of the <code>attachentURLXPath</code> attribute.
     */
    public String getAttachentURLXPath()
    {
        return get(attachentURLXPathDef);
    }
    
    /**
     * Returns the value of the <code>attachentURLXPath</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>attachentURLXPath</code> attribute.
     */
    public String getAttachentURLXPath(String defaultValue)
    {
        return get(attachentURLXPathDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>attachentURLXPath</code> attribute.
     *
     * @param value the value of the <code>attachentURLXPath</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAttachentURLXPath(String value)
    {
        try
        {
            if(value != null)
            {
                set(attachentURLXPathDef, value);
            }
            else
            {
                unset(attachentURLXPathDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>attachentURLXPath</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>attachentURLXPath</code> attribute is defined.
	 */
    public boolean isAttachentURLXPathDefined()
	{
	    return isDefined(attachentURLXPathDef);
	}

    /**
     * Returns the value of the <code>attachmentURLComposite</code> attribute.
     *
     * @return the value of the <code>attachmentURLComposite</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getAttachmentURLComposite()
        throws IllegalStateException
    {
	    Boolean value = get(attachmentURLCompositeDef);
        if(value != null)
        {
            return value.booleanValue();
        }
        else
        {
            throw new IllegalStateException("value of attribute attachmentURLComposite is undefined"+
			    " for resource #"+getId());
        }
    }

    /**
     * Returns the value of the <code>attachmentURLComposite</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>attachmentURLComposite</code> attribute.
     */
    public boolean getAttachmentURLComposite(boolean defaultValue)
    {
		return get(attachmentURLCompositeDef, Boolean.valueOf(defaultValue)).booleanValue();
	}

    /**
     * Sets the value of the <code>attachmentURLComposite</code> attribute.
     *
     * @param value the value of the <code>attachmentURLComposite</code> attribute.
     */
    public void setAttachmentURLComposite(boolean value)
    {
        try
        {
            set(attachmentURLCompositeDef, Boolean.valueOf(value));
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
	
	/**
     * Removes the value of the <code>attachmentURLComposite</code> attribute.
     */
    public void unsetAttachmentURLComposite()
    {
        try
        {
            unset(attachmentURLCompositeDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>attachmentURLComposite</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>attachmentURLComposite</code> attribute is defined.
	 */
    public boolean isAttachmentURLCompositeDefined()
	{
	    return isDefined(attachmentURLCompositeDef);
	}
 
    /**
     * Returns the value of the <code>attachmentURLSeparator</code> attribute.
     *
     * @return the value of the <code>attachmentURLSeparator</code> attribute.
     */
    public String getAttachmentURLSeparator()
    {
        return get(attachmentURLSeparatorDef);
    }
    
    /**
     * Returns the value of the <code>attachmentURLSeparator</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>attachmentURLSeparator</code> attribute.
     */
    public String getAttachmentURLSeparator(String defaultValue)
    {
        return get(attachmentURLSeparatorDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>attachmentURLSeparator</code> attribute.
     *
     * @param value the value of the <code>attachmentURLSeparator</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAttachmentURLSeparator(String value)
    {
        try
        {
            if(value != null)
            {
                set(attachmentURLSeparatorDef, value);
            }
            else
            {
                unset(attachmentURLSeparatorDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>attachmentURLSeparator</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>attachmentURLSeparator</code> attribute is defined.
	 */
    public boolean isAttachmentURLSeparatorDefined()
	{
	    return isDefined(attachmentURLSeparatorDef);
	}
 
    /**
     * Returns the value of the <code>attachmentsLocation</code> attribute.
     *
     * @return the value of the <code>attachmentsLocation</code> attribute.
     */
    public Resource getAttachmentsLocation()
    {
        return get(attachmentsLocationDef);
    }
 
    /**
     * Sets the value of the <code>attachmentsLocation</code> attribute.
     *
     * @param value the value of the <code>attachmentsLocation</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setAttachmentsLocation(Resource value)
        throws ValueRequiredException
    {
        try
        {
            if(value != null)
            {
                set(attachmentsLocationDef, value);
            }
            else
            {
                throw new ValueRequiredException("attribute attachmentsLocation "+
                                                 "is declared as REQUIRED");
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
    
    /**
     * Returns the value of the <code>calendarStructureType</code> attribute.
     *
     * @return the value of the <code>calendarStructureType</code> attribute.
     */
    public String getCalendarStructureType()
    {
        return get(calendarStructureTypeDef);
    }
    
    /**
     * Returns the value of the <code>calendarStructureType</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>calendarStructureType</code> attribute.
     */
    public String getCalendarStructureType(String defaultValue)
    {
        return get(calendarStructureTypeDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>calendarStructureType</code> attribute.
     *
     * @param value the value of the <code>calendarStructureType</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setCalendarStructureType(String value)
    {
        try
        {
            if(value != null)
            {
                set(calendarStructureTypeDef, value);
            }
            else
            {
                unset(calendarStructureTypeDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>calendarStructureType</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>calendarStructureType</code> attribute is defined.
	 */
    public boolean isCalendarStructureTypeDefined()
	{
	    return isDefined(calendarStructureTypeDef);
	}
 
    /**
     * Returns the value of the <code>categories</code> attribute.
     *
     * @return the value of the <code>categories</code> attribute.
     */
    public ResourceList getCategories()
    {
        return get(categoriesDef);
    }
    
    /**
     * Returns the value of the <code>categories</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>categories</code> attribute.
     */
    public ResourceList getCategories(ResourceList defaultValue)
    {
        return get(categoriesDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>categories</code> attribute.
     *
     * @param value the value of the <code>categories</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setCategories(ResourceList value)
    {
        try
        {
            if(value != null)
            {
                set(categoriesDef, value);
            }
            else
            {
                unset(categoriesDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>categories</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>categories</code> attribute is defined.
	 */
    public boolean isCategoriesDefined()
	{
	    return isDefined(categoriesDef);
	}
 
    /**
     * Returns the value of the <code>contentCleanupProfile</code> attribute.
     *
     * @return the value of the <code>contentCleanupProfile</code> attribute.
     */
    public String getContentCleanupProfile()
    {
        return get(contentCleanupProfileDef);
    }
    
    /**
     * Returns the value of the <code>contentCleanupProfile</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>contentCleanupProfile</code> attribute.
     */
    public String getContentCleanupProfile(String defaultValue)
    {
        return get(contentCleanupProfileDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>contentCleanupProfile</code> attribute.
     *
     * @param value the value of the <code>contentCleanupProfile</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setContentCleanupProfile(String value)
    {
        try
        {
            if(value != null)
            {
                set(contentCleanupProfileDef, value);
            }
            else
            {
                unset(contentCleanupProfileDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>contentCleanupProfile</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>contentCleanupProfile</code> attribute is defined.
	 */
    public boolean isContentCleanupProfileDefined()
	{
	    return isDefined(contentCleanupProfileDef);
	}

    /**
     * Returns the value of the <code>contentEntitytEncoded</code> attribute.
     *
     * @return the value of the <code>contentEntitytEncoded</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getContentEntitytEncoded()
        throws IllegalStateException
    {
	    Boolean value = get(contentEntitytEncodedDef);
        if(value != null)
        {
            return value.booleanValue();
        }
        else
        {
            throw new IllegalStateException("value of attribute contentEntitytEncoded is undefined"+
			    " for resource #"+getId());
        }
    }

    /**
     * Returns the value of the <code>contentEntitytEncoded</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>contentEntitytEncoded</code> attribute.
     */
    public boolean getContentEntitytEncoded(boolean defaultValue)
    {
		return get(contentEntitytEncodedDef, Boolean.valueOf(defaultValue)).booleanValue();
	}

    /**
     * Sets the value of the <code>contentEntitytEncoded</code> attribute.
     *
     * @param value the value of the <code>contentEntitytEncoded</code> attribute.
     */
    public void setContentEntitytEncoded(boolean value)
    {
        try
        {
            set(contentEntitytEncodedDef, Boolean.valueOf(value));
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
	
	/**
     * Removes the value of the <code>contentEntitytEncoded</code> attribute.
     */
    public void unsetContentEntitytEncoded()
    {
        try
        {
            unset(contentEntitytEncodedDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>contentEntitytEncoded</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>contentEntitytEncoded</code> attribute is defined.
	 */
    public boolean isContentEntitytEncodedDefined()
	{
	    return isDefined(contentEntitytEncodedDef);
	}
 
    /**
     * Returns the value of the <code>contentXPath</code> attribute.
     *
     * @return the value of the <code>contentXPath</code> attribute.
     */
    public String getContentXPath()
    {
        return get(contentXPathDef);
    }
    
    /**
     * Returns the value of the <code>contentXPath</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>contentXPath</code> attribute.
     */
    public String getContentXPath(String defaultValue)
    {
        return get(contentXPathDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>contentXPath</code> attribute.
     *
     * @param value the value of the <code>contentXPath</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setContentXPath(String value)
    {
        try
        {
            if(value != null)
            {
                set(contentXPathDef, value);
            }
            else
            {
                unset(contentXPathDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>contentXPath</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>contentXPath</code> attribute is defined.
	 */
    public boolean isContentXPathDefined()
	{
	    return isDefined(contentXPathDef);
	}
 
    /**
     * Returns the value of the <code>creationDateXPath</code> attribute.
     *
     * @return the value of the <code>creationDateXPath</code> attribute.
     */
    public String getCreationDateXPath()
    {
        return get(creationDateXPathDef);
    }
    
    /**
     * Returns the value of the <code>creationDateXPath</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>creationDateXPath</code> attribute.
     */
    public String getCreationDateXPath(String defaultValue)
    {
        return get(creationDateXPathDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>creationDateXPath</code> attribute.
     *
     * @param value the value of the <code>creationDateXPath</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setCreationDateXPath(String value)
    {
        try
        {
            if(value != null)
            {
                set(creationDateXPathDef, value);
            }
            else
            {
                unset(creationDateXPathDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>creationDateXPath</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>creationDateXPath</code> attribute is defined.
	 */
    public boolean isCreationDateXPathDefined()
	{
	    return isDefined(creationDateXPathDef);
	}
 
    /**
     * Returns the value of the <code>dateFormat</code> attribute.
     *
     * @return the value of the <code>dateFormat</code> attribute.
     */
    public String getDateFormat()
    {
        return get(dateFormatDef);
    }
    
    /**
     * Returns the value of the <code>dateFormat</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>dateFormat</code> attribute.
     */
    public String getDateFormat(String defaultValue)
    {
        return get(dateFormatDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>dateFormat</code> attribute.
     *
     * @param value the value of the <code>dateFormat</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setDateFormat(String value)
    {
        try
        {
            if(value != null)
            {
                set(dateFormatDef, value);
            }
            else
            {
                unset(dateFormatDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>dateFormat</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>dateFormat</code> attribute is defined.
	 */
    public boolean isDateFormatDefined()
	{
	    return isDefined(dateFormatDef);
	}
 
    /**
     * Returns the value of the <code>dateRangeEndParameter</code> attribute.
     *
     * @return the value of the <code>dateRangeEndParameter</code> attribute.
     */
    public String getDateRangeEndParameter()
    {
        return get(dateRangeEndParameterDef);
    }
    
    /**
     * Returns the value of the <code>dateRangeEndParameter</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>dateRangeEndParameter</code> attribute.
     */
    public String getDateRangeEndParameter(String defaultValue)
    {
        return get(dateRangeEndParameterDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>dateRangeEndParameter</code> attribute.
     *
     * @param value the value of the <code>dateRangeEndParameter</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setDateRangeEndParameter(String value)
    {
        try
        {
            if(value != null)
            {
                set(dateRangeEndParameterDef, value);
            }
            else
            {
                unset(dateRangeEndParameterDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>dateRangeEndParameter</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>dateRangeEndParameter</code> attribute is defined.
	 */
    public boolean isDateRangeEndParameterDefined()
	{
	    return isDefined(dateRangeEndParameterDef);
	}
 
    /**
     * Returns the value of the <code>dateRangeStartParameter</code> attribute.
     *
     * @return the value of the <code>dateRangeStartParameter</code> attribute.
     */
    public String getDateRangeStartParameter()
    {
        return get(dateRangeStartParameterDef);
    }
    
    /**
     * Returns the value of the <code>dateRangeStartParameter</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>dateRangeStartParameter</code> attribute.
     */
    public String getDateRangeStartParameter(String defaultValue)
    {
        return get(dateRangeStartParameterDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>dateRangeStartParameter</code> attribute.
     *
     * @param value the value of the <code>dateRangeStartParameter</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setDateRangeStartParameter(String value)
    {
        try
        {
            if(value != null)
            {
                set(dateRangeStartParameterDef, value);
            }
            else
            {
                unset(dateRangeStartParameterDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>dateRangeStartParameter</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>dateRangeStartParameter</code> attribute is defined.
	 */
    public boolean isDateRangeStartParameterDefined()
	{
	    return isDefined(dateRangeStartParameterDef);
	}
 
    /**
     * Returns the value of the <code>documentXPath</code> attribute.
     *
     * @return the value of the <code>documentXPath</code> attribute.
     */
    public String getDocumentXPath()
    {
        return get(documentXPathDef);
    }
    
    /**
     * Returns the value of the <code>documentXPath</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>documentXPath</code> attribute.
     */
    public String getDocumentXPath(String defaultValue)
    {
        return get(documentXPathDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>documentXPath</code> attribute.
     *
     * @param value the value of the <code>documentXPath</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setDocumentXPath(String value)
    {
        try
        {
            if(value != null)
            {
                set(documentXPathDef, value);
            }
            else
            {
                unset(documentXPathDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>documentXPath</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>documentXPath</code> attribute is defined.
	 */
    public boolean isDocumentXPathDefined()
	{
	    return isDefined(documentXPathDef);
	}
 
    /**
     * Returns the value of the <code>footer</code> attribute.
     *
     * @return the value of the <code>footer</code> attribute.
     */
    public String getFooter()
    {
        return get(footerDef);
    }
    
    /**
     * Returns the value of the <code>footer</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>footer</code> attribute.
     */
    public String getFooter(String defaultValue)
    {
        return get(footerDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>footer</code> attribute.
     *
     * @param value the value of the <code>footer</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setFooter(String value)
    {
        try
        {
            if(value != null)
            {
                set(footerDef, value);
            }
            else
            {
                unset(footerDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>footer</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>footer</code> attribute is defined.
	 */
    public boolean isFooterDefined()
	{
	    return isDefined(footerDef);
	}
 
    /**
     * Returns the value of the <code>lastNewDocumentsCheck</code> attribute.
     *
     * @return the value of the <code>lastNewDocumentsCheck</code> attribute.
     */
    public Date getLastNewDocumentsCheck()
    {
        return get(lastNewDocumentsCheckDef);
    }
    
    /**
     * Returns the value of the <code>lastNewDocumentsCheck</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>lastNewDocumentsCheck</code> attribute.
     */
    public Date getLastNewDocumentsCheck(Date defaultValue)
    {
        return get(lastNewDocumentsCheckDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>lastNewDocumentsCheck</code> attribute.
     *
     * @param value the value of the <code>lastNewDocumentsCheck</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setLastNewDocumentsCheck(Date value)
    {
        try
        {
            if(value != null)
            {
                set(lastNewDocumentsCheckDef, value);
            }
            else
            {
                unset(lastNewDocumentsCheckDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>lastNewDocumentsCheck</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>lastNewDocumentsCheck</code> attribute is defined.
	 */
    public boolean isLastNewDocumentsCheckDefined()
	{
	    return isDefined(lastNewDocumentsCheckDef);
	}
 
    /**
     * Returns the value of the <code>lastUpdatedDocumentsCheck</code> attribute.
     *
     * @return the value of the <code>lastUpdatedDocumentsCheck</code> attribute.
     */
    public Date getLastUpdatedDocumentsCheck()
    {
        return get(lastUpdatedDocumentsCheckDef);
    }
    
    /**
     * Returns the value of the <code>lastUpdatedDocumentsCheck</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>lastUpdatedDocumentsCheck</code> attribute.
     */
    public Date getLastUpdatedDocumentsCheck(Date defaultValue)
    {
        return get(lastUpdatedDocumentsCheckDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>lastUpdatedDocumentsCheck</code> attribute.
     *
     * @param value the value of the <code>lastUpdatedDocumentsCheck</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setLastUpdatedDocumentsCheck(Date value)
    {
        try
        {
            if(value != null)
            {
                set(lastUpdatedDocumentsCheckDef, value);
            }
            else
            {
                unset(lastUpdatedDocumentsCheckDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>lastUpdatedDocumentsCheck</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>lastUpdatedDocumentsCheck</code> attribute is defined.
	 */
    public boolean isLastUpdatedDocumentsCheckDefined()
	{
	    return isDefined(lastUpdatedDocumentsCheckDef);
	}
 
    /**
     * Returns the value of the <code>location</code> attribute.
     *
     * @return the value of the <code>location</code> attribute.
     */
    public String getLocation()
    {
        return get(locationDef);
    }
 
    /**
     * Sets the value of the <code>location</code> attribute.
     *
     * @param value the value of the <code>location</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setLocation(String value)
        throws ValueRequiredException
    {
        try
        {
            if(value != null)
            {
                set(locationDef, value);
            }
            else
            {
                throw new ValueRequiredException("attribute location "+
                                                 "is declared as REQUIRED");
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
    
    /**
     * Returns the value of the <code>modificationDateXPath</code> attribute.
     *
     * @return the value of the <code>modificationDateXPath</code> attribute.
     */
    public String getModificationDateXPath()
    {
        return get(modificationDateXPathDef);
    }
    
    /**
     * Returns the value of the <code>modificationDateXPath</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>modificationDateXPath</code> attribute.
     */
    public String getModificationDateXPath(String defaultValue)
    {
        return get(modificationDateXPathDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>modificationDateXPath</code> attribute.
     *
     * @param value the value of the <code>modificationDateXPath</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setModificationDateXPath(String value)
    {
        try
        {
            if(value != null)
            {
                set(modificationDateXPathDef, value);
            }
            else
            {
                unset(modificationDateXPathDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>modificationDateXPath</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>modificationDateXPath</code> attribute is defined.
	 */
    public boolean isModificationDateXPathDefined()
	{
	    return isDefined(modificationDateXPathDef);
	}
 
    /**
     * Returns the value of the <code>originalURLXPath</code> attribute.
     *
     * @return the value of the <code>originalURLXPath</code> attribute.
     */
    public String getOriginalURLXPath()
    {
        return get(originalURLXPathDef);
    }
    
    /**
     * Returns the value of the <code>originalURLXPath</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>originalURLXPath</code> attribute.
     */
    public String getOriginalURLXPath(String defaultValue)
    {
        return get(originalURLXPathDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>originalURLXPath</code> attribute.
     *
     * @param value the value of the <code>originalURLXPath</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setOriginalURLXPath(String value)
    {
        try
        {
            if(value != null)
            {
                set(originalURLXPathDef, value);
            }
            else
            {
                unset(originalURLXPathDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>originalURLXPath</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>originalURLXPath</code> attribute is defined.
	 */
    public boolean isOriginalURLXPathDefined()
	{
	    return isDefined(originalURLXPathDef);
	}
 
    /**
     * Returns the value of the <code>ownerLogin</code> attribute.
     *
     * @return the value of the <code>ownerLogin</code> attribute.
     */
    public String getOwnerLogin()
    {
        return get(ownerLoginDef);
    }
 
    /**
     * Sets the value of the <code>ownerLogin</code> attribute.
     *
     * @param value the value of the <code>ownerLogin</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setOwnerLogin(String value)
        throws ValueRequiredException
    {
        try
        {
            if(value != null)
            {
                set(ownerLoginDef, value);
            }
            else
            {
                throw new ValueRequiredException("attribute ownerLogin "+
                                                 "is declared as REQUIRED");
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
    
    /**
     * Returns the value of the <code>sourceName</code> attribute.
     *
     * @return the value of the <code>sourceName</code> attribute.
     */
    public String getSourceName()
    {
        return get(sourceNameDef);
    }
 
    /**
     * Sets the value of the <code>sourceName</code> attribute.
     *
     * @param value the value of the <code>sourceName</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setSourceName(String value)
        throws ValueRequiredException
    {
        try
        {
            if(value != null)
            {
                set(sourceNameDef, value);
            }
            else
            {
                throw new ValueRequiredException("attribute sourceName "+
                                                 "is declared as REQUIRED");
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
    
    /**
     * Returns the value of the <code>targetLocation</code> attribute.
     *
     * @return the value of the <code>targetLocation</code> attribute.
     */
    public Resource getTargetLocation()
    {
        return get(targetLocationDef);
    }
 
    /**
     * Sets the value of the <code>targetLocation</code> attribute.
     *
     * @param value the value of the <code>targetLocation</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setTargetLocation(Resource value)
        throws ValueRequiredException
    {
        try
        {
            if(value != null)
            {
                set(targetLocationDef, value);
            }
            else
            {
                throw new ValueRequiredException("attribute targetLocation "+
                                                 "is declared as REQUIRED");
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
    
    /**
     * Returns the value of the <code>titleCleanupProfile</code> attribute.
     *
     * @return the value of the <code>titleCleanupProfile</code> attribute.
     */
    public String getTitleCleanupProfile()
    {
        return get(titleCleanupProfileDef);
    }
    
    /**
     * Returns the value of the <code>titleCleanupProfile</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>titleCleanupProfile</code> attribute.
     */
    public String getTitleCleanupProfile(String defaultValue)
    {
        return get(titleCleanupProfileDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>titleCleanupProfile</code> attribute.
     *
     * @param value the value of the <code>titleCleanupProfile</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setTitleCleanupProfile(String value)
    {
        try
        {
            if(value != null)
            {
                set(titleCleanupProfileDef, value);
            }
            else
            {
                unset(titleCleanupProfileDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>titleCleanupProfile</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>titleCleanupProfile</code> attribute is defined.
	 */
    public boolean isTitleCleanupProfileDefined()
	{
	    return isDefined(titleCleanupProfileDef);
	}

    /**
     * Returns the value of the <code>titleEntityEncoded</code> attribute.
     *
     * @return the value of the <code>titleEntityEncoded</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getTitleEntityEncoded()
        throws IllegalStateException
    {
	    Boolean value = get(titleEntityEncodedDef);
        if(value != null)
        {
            return value.booleanValue();
        }
        else
        {
            throw new IllegalStateException("value of attribute titleEntityEncoded is undefined"+
			    " for resource #"+getId());
        }
    }

    /**
     * Returns the value of the <code>titleEntityEncoded</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>titleEntityEncoded</code> attribute.
     */
    public boolean getTitleEntityEncoded(boolean defaultValue)
    {
		return get(titleEntityEncodedDef, Boolean.valueOf(defaultValue)).booleanValue();
	}

    /**
     * Sets the value of the <code>titleEntityEncoded</code> attribute.
     *
     * @param value the value of the <code>titleEntityEncoded</code> attribute.
     */
    public void setTitleEntityEncoded(boolean value)
    {
        try
        {
            set(titleEntityEncodedDef, Boolean.valueOf(value));
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
	
	/**
     * Removes the value of the <code>titleEntityEncoded</code> attribute.
     */
    public void unsetTitleEntityEncoded()
    {
        try
        {
            unset(titleEntityEncodedDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>titleEntityEncoded</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>titleEntityEncoded</code> attribute is defined.
	 */
    public boolean isTitleEntityEncodedDefined()
	{
	    return isDefined(titleEntityEncodedDef);
	}
 
    /**
     * Returns the value of the <code>titleXPath</code> attribute.
     *
     * @return the value of the <code>titleXPath</code> attribute.
     */
    public String getTitleXPath()
    {
        return get(titleXPathDef);
    }
    
    /**
     * Returns the value of the <code>titleXPath</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>titleXPath</code> attribute.
     */
    public String getTitleXPath(String defaultValue)
    {
        return get(titleXPathDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>titleXPath</code> attribute.
     *
     * @param value the value of the <code>titleXPath</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setTitleXPath(String value)
    {
        try
        {
            if(value != null)
            {
                set(titleXPathDef, value);
            }
            else
            {
                unset(titleXPathDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>titleXPath</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>titleXPath</code> attribute is defined.
	 */
    public boolean isTitleXPathDefined()
	{
	    return isDefined(titleXPathDef);
	}
  
    // @custom methods ///////////////////////////////////////////////////////
}
