// 
// Copyright (c) 2004, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
// 
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
// 
// * Redistributions of source code must retain the above copyright notice,  
//       this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//       this list of conditions and the following disclaimer in the documentation  
//       and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//       nor the names of its contributors may be used to endorse or promote products  
//       derived from this software without specific prior written permission. 
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
// IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,  
// INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  
// BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
// OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,  
// WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)  
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  
// POSSIBILITY OF SUCH DAMAGE. 
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
    private static boolean definitionsInitialized;
	
    /** The AttributeDefinition object for the <code>abstractCleanupProfile</code> attribute. */
    private static AttributeDefinition abstractCleanupProfileDef;

    /** The AttributeDefinition object for the <code>abstractEntityEncoded</code> attribute. */
    private static AttributeDefinition abstractEntityEncodedDef;

    /** The AttributeDefinition object for the <code>abstractXPath</code> attribute. */
    private static AttributeDefinition abstractXPathDef;

    /** The AttributeDefinition object for the <code>attachentURLXPath</code> attribute. */
    private static AttributeDefinition attachentURLXPathDef;

    /** The AttributeDefinition object for the <code>attachmentURLComposite</code> attribute. */
    private static AttributeDefinition attachmentURLCompositeDef;

    /** The AttributeDefinition object for the <code>attachmentURLSeparator</code> attribute. */
    private static AttributeDefinition attachmentURLSeparatorDef;

    /** The AttributeDefinition object for the <code>attachmentsLocation</code> attribute. */
    private static AttributeDefinition attachmentsLocationDef;

    /** The AttributeDefinition object for the <code>calendarStructureType</code> attribute. */
    private static AttributeDefinition calendarStructureTypeDef;

    /** The AttributeDefinition object for the <code>categories</code> attribute. */
    private static AttributeDefinition categoriesDef;

    /** The AttributeDefinition object for the <code>contentCleanupProfile</code> attribute. */
    private static AttributeDefinition contentCleanupProfileDef;

    /** The AttributeDefinition object for the <code>contentEntitytEncoded</code> attribute. */
    private static AttributeDefinition contentEntitytEncodedDef;

    /** The AttributeDefinition object for the <code>contentXPath</code> attribute. */
    private static AttributeDefinition contentXPathDef;

    /** The AttributeDefinition object for the <code>creationDateXPath</code> attribute. */
    private static AttributeDefinition creationDateXPathDef;

    /** The AttributeDefinition object for the <code>dateFormat</code> attribute. */
    private static AttributeDefinition dateFormatDef;

    /** The AttributeDefinition object for the <code>dateRangeEndParameter</code> attribute. */
    private static AttributeDefinition dateRangeEndParameterDef;

    /** The AttributeDefinition object for the <code>dateRangeStartParameter</code> attribute. */
    private static AttributeDefinition dateRangeStartParameterDef;

    /** The AttributeDefinition object for the <code>documentXPath</code> attribute. */
    private static AttributeDefinition documentXPathDef;

    /** The AttributeDefinition object for the <code>footer</code> attribute. */
    private static AttributeDefinition footerDef;

    /** The AttributeDefinition object for the <code>lastNewDocumentsCheck</code> attribute. */
    private static AttributeDefinition lastNewDocumentsCheckDef;

    /** The AttributeDefinition object for the <code>lastUpdatedDocumentsCheck</code> attribute. */
    private static AttributeDefinition lastUpdatedDocumentsCheckDef;

    /** The AttributeDefinition object for the <code>location</code> attribute. */
    private static AttributeDefinition locationDef;

    /** The AttributeDefinition object for the <code>modificationDateXPath</code> attribute. */
    private static AttributeDefinition modificationDateXPathDef;

    /** The AttributeDefinition object for the <code>originalURLXPath</code> attribute. */
    private static AttributeDefinition originalURLXPathDef;

    /** The AttributeDefinition object for the <code>ownerLogin</code> attribute. */
    private static AttributeDefinition ownerLoginDef;

    /** The AttributeDefinition object for the <code>sourceName</code> attribute. */
    private static AttributeDefinition sourceNameDef;

    /** The AttributeDefinition object for the <code>targetLocation</code> attribute. */
    private static AttributeDefinition targetLocationDef;

    /** The AttributeDefinition object for the <code>titleCleanupProfile</code> attribute. */
    private static AttributeDefinition titleCleanupProfileDef;

    /** The AttributeDefinition object for the <code>titleEntityEncoded</code> attribute. */
    private static AttributeDefinition titleEntityEncodedDef;

    /** The AttributeDefinition object for the <code>titleXPath</code> attribute. */
    private static AttributeDefinition titleXPathDef;

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
            ResourceClass rc = session.getSchema().getResourceClass("docimport.import");
            Map attrs = new HashMap();
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
        return (String)getInternal(abstractCleanupProfileDef, null);
    }
    
    /**
     * Returns the value of the <code>abstractCleanupProfile</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>abstractCleanupProfile</code> attribute.
     */
    public String getAbstractCleanupProfile(String defaultValue)
    {
        return (String)getInternal(abstractCleanupProfileDef, defaultValue);
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
	    Boolean value = (Boolean)getInternal(abstractEntityEncodedDef, null);
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
		return ((Boolean)getInternal(abstractEntityEncodedDef, new Boolean(defaultValue))).booleanValue();
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
            set(abstractEntityEncodedDef, new Boolean(value));
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
        return (String)getInternal(abstractXPathDef, null);
    }
    
    /**
     * Returns the value of the <code>abstractXPath</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>abstractXPath</code> attribute.
     */
    public String getAbstractXPath(String defaultValue)
    {
        return (String)getInternal(abstractXPathDef, defaultValue);
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
        return (String)getInternal(attachentURLXPathDef, null);
    }
    
    /**
     * Returns the value of the <code>attachentURLXPath</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>attachentURLXPath</code> attribute.
     */
    public String getAttachentURLXPath(String defaultValue)
    {
        return (String)getInternal(attachentURLXPathDef, defaultValue);
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
	    Boolean value = (Boolean)getInternal(attachmentURLCompositeDef, null);
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
		return ((Boolean)getInternal(attachmentURLCompositeDef, new Boolean(defaultValue))).booleanValue();
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
            set(attachmentURLCompositeDef, new Boolean(value));
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
        return (String)getInternal(attachmentURLSeparatorDef, null);
    }
    
    /**
     * Returns the value of the <code>attachmentURLSeparator</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>attachmentURLSeparator</code> attribute.
     */
    public String getAttachmentURLSeparator(String defaultValue)
    {
        return (String)getInternal(attachmentURLSeparatorDef, defaultValue);
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
        return (Resource)getInternal(attachmentsLocationDef, null);
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
        return (String)getInternal(calendarStructureTypeDef, null);
    }
    
    /**
     * Returns the value of the <code>calendarStructureType</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>calendarStructureType</code> attribute.
     */
    public String getCalendarStructureType(String defaultValue)
    {
        return (String)getInternal(calendarStructureTypeDef, defaultValue);
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
        return (ResourceList)getInternal(categoriesDef, null);
    }
    
    /**
     * Returns the value of the <code>categories</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>categories</code> attribute.
     */
    public ResourceList getCategories(ResourceList defaultValue)
    {
        return (ResourceList)getInternal(categoriesDef, defaultValue);
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
        return (String)getInternal(contentCleanupProfileDef, null);
    }
    
    /**
     * Returns the value of the <code>contentCleanupProfile</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>contentCleanupProfile</code> attribute.
     */
    public String getContentCleanupProfile(String defaultValue)
    {
        return (String)getInternal(contentCleanupProfileDef, defaultValue);
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
	    Boolean value = (Boolean)getInternal(contentEntitytEncodedDef, null);
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
		return ((Boolean)getInternal(contentEntitytEncodedDef, new Boolean(defaultValue))).booleanValue();
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
            set(contentEntitytEncodedDef, new Boolean(value));
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
        return (String)getInternal(contentXPathDef, null);
    }
    
    /**
     * Returns the value of the <code>contentXPath</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>contentXPath</code> attribute.
     */
    public String getContentXPath(String defaultValue)
    {
        return (String)getInternal(contentXPathDef, defaultValue);
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
        return (String)getInternal(creationDateXPathDef, null);
    }
    
    /**
     * Returns the value of the <code>creationDateXPath</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>creationDateXPath</code> attribute.
     */
    public String getCreationDateXPath(String defaultValue)
    {
        return (String)getInternal(creationDateXPathDef, defaultValue);
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
        return (String)getInternal(dateFormatDef, null);
    }
    
    /**
     * Returns the value of the <code>dateFormat</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>dateFormat</code> attribute.
     */
    public String getDateFormat(String defaultValue)
    {
        return (String)getInternal(dateFormatDef, defaultValue);
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
        return (String)getInternal(dateRangeEndParameterDef, null);
    }
    
    /**
     * Returns the value of the <code>dateRangeEndParameter</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>dateRangeEndParameter</code> attribute.
     */
    public String getDateRangeEndParameter(String defaultValue)
    {
        return (String)getInternal(dateRangeEndParameterDef, defaultValue);
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
        return (String)getInternal(dateRangeStartParameterDef, null);
    }
    
    /**
     * Returns the value of the <code>dateRangeStartParameter</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>dateRangeStartParameter</code> attribute.
     */
    public String getDateRangeStartParameter(String defaultValue)
    {
        return (String)getInternal(dateRangeStartParameterDef, defaultValue);
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
        return (String)getInternal(documentXPathDef, null);
    }
    
    /**
     * Returns the value of the <code>documentXPath</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>documentXPath</code> attribute.
     */
    public String getDocumentXPath(String defaultValue)
    {
        return (String)getInternal(documentXPathDef, defaultValue);
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
        return (String)getInternal(footerDef, null);
    }
    
    /**
     * Returns the value of the <code>footer</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>footer</code> attribute.
     */
    public String getFooter(String defaultValue)
    {
        return (String)getInternal(footerDef, defaultValue);
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
        return (Date)getInternal(lastNewDocumentsCheckDef, null);
    }
    
    /**
     * Returns the value of the <code>lastNewDocumentsCheck</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>lastNewDocumentsCheck</code> attribute.
     */
    public Date getLastNewDocumentsCheck(Date defaultValue)
    {
        return (Date)getInternal(lastNewDocumentsCheckDef, defaultValue);
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
        return (Date)getInternal(lastUpdatedDocumentsCheckDef, null);
    }
    
    /**
     * Returns the value of the <code>lastUpdatedDocumentsCheck</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>lastUpdatedDocumentsCheck</code> attribute.
     */
    public Date getLastUpdatedDocumentsCheck(Date defaultValue)
    {
        return (Date)getInternal(lastUpdatedDocumentsCheckDef, defaultValue);
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
        return (String)getInternal(locationDef, null);
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
        return (String)getInternal(modificationDateXPathDef, null);
    }
    
    /**
     * Returns the value of the <code>modificationDateXPath</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>modificationDateXPath</code> attribute.
     */
    public String getModificationDateXPath(String defaultValue)
    {
        return (String)getInternal(modificationDateXPathDef, defaultValue);
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
        return (String)getInternal(originalURLXPathDef, null);
    }
    
    /**
     * Returns the value of the <code>originalURLXPath</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>originalURLXPath</code> attribute.
     */
    public String getOriginalURLXPath(String defaultValue)
    {
        return (String)getInternal(originalURLXPathDef, defaultValue);
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
        return (String)getInternal(ownerLoginDef, null);
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
        return (String)getInternal(sourceNameDef, null);
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
        return (Resource)getInternal(targetLocationDef, null);
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
        return (String)getInternal(titleCleanupProfileDef, null);
    }
    
    /**
     * Returns the value of the <code>titleCleanupProfile</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>titleCleanupProfile</code> attribute.
     */
    public String getTitleCleanupProfile(String defaultValue)
    {
        return (String)getInternal(titleCleanupProfileDef, defaultValue);
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
	    Boolean value = (Boolean)getInternal(titleEntityEncodedDef, null);
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
		return ((Boolean)getInternal(titleEntityEncodedDef, new Boolean(defaultValue))).booleanValue();
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
            set(titleEntityEncodedDef, new Boolean(value));
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
        return (String)getInternal(titleXPathDef, null);
    }
    
    /**
     * Returns the value of the <code>titleXPath</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>titleXPath</code> attribute.
     */
    public String getTitleXPath(String defaultValue)
    {
        return (String)getInternal(titleXPathDef, defaultValue);
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
