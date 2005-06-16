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
 
package net.cyklotron.cms.syndication;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.objectledge.coral.BackendException;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.CoralSchema;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.ModificationNotPermitedException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;
import org.objectledge.database.Database;

import net.cyklotron.cms.CmsNodeResourceImpl;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import org.jcontainer.dna.Logger;

/**
 * An implementation of <code>cms.syndication.outgoingfeed</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class OutgoingFeedResourceImpl
    extends CmsNodeResourceImpl
    implements OutgoingFeedResource
{
    // instance variables ////////////////////////////////////////////////////

    /** The AttributeDefinition object for the <code>category</code> attribute. */
    private AttributeDefinition categoryDef;

    /** The AttributeDefinition object for the <code>categoryQuery</code> attribute. */
    private AttributeDefinition categoryQueryDef;

    /** The AttributeDefinition object for the <code>contents</code> attribute. */
    private AttributeDefinition contentsDef;

    /** The AttributeDefinition object for the <code>copyright</code> attribute. */
    private AttributeDefinition copyrightDef;

    /** The AttributeDefinition object for the <code>generationTemplate</code> attribute. */
    private AttributeDefinition generationTemplateDef;

    /** The AttributeDefinition object for the <code>interval</code> attribute. */
    private AttributeDefinition intervalDef;

    /** The AttributeDefinition object for the <code>language</code> attribute. */
    private AttributeDefinition languageDef;

    /** The AttributeDefinition object for the <code>lastUpdate</code> attribute. */
    private AttributeDefinition lastUpdateDef;

    /** The AttributeDefinition object for the <code>managingEditor</code> attribute. */
    private AttributeDefinition managingEditorDef;

    /** The AttributeDefinition object for the <code>public</code> attribute. */
    private AttributeDefinition publicDef;

    /** The AttributeDefinition object for the <code>webMaster</code> attribute. */
    private AttributeDefinition webMasterDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>cms.syndication.outgoingfeed</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     * @param schema the CoralSchema.
     * @param database the Database.
     * @param logger the Logger.
     */
    public OutgoingFeedResourceImpl(CoralSchema schema, Database database, Logger logger)
    {
        super(schema, database, logger);
        try
        {
            ResourceClass rc = schema.getResourceClass("cms.syndication.outgoingfeed");
            categoryDef = rc.getAttribute("category");
            categoryQueryDef = rc.getAttribute("categoryQuery");
            contentsDef = rc.getAttribute("contents");
            copyrightDef = rc.getAttribute("copyright");
            generationTemplateDef = rc.getAttribute("generationTemplate");
            intervalDef = rc.getAttribute("interval");
            languageDef = rc.getAttribute("language");
            lastUpdateDef = rc.getAttribute("lastUpdate");
            managingEditorDef = rc.getAttribute("managingEditor");
            publicDef = rc.getAttribute("public");
            webMasterDef = rc.getAttribute("webMaster");
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>cms.syndication.outgoingfeed</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static OutgoingFeedResource getOutgoingFeedResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof OutgoingFeedResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not cms.syndication.outgoingfeed");
        }
        return (OutgoingFeedResource)res;
    }

    /**
     * Creates a new <code>cms.syndication.outgoingfeed</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @return a new OutgoingFeedResource instance.
     * @throws InvalidResourceNameException if the name argument contains illegal characters.
     */
    public static OutgoingFeedResource createOutgoingFeedResource(CoralSession session, String
        name, Resource parent)
        throws InvalidResourceNameException
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("cms.syndication.outgoingfeed");
            Map attrs = new HashMap();
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof OutgoingFeedResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (OutgoingFeedResource)res;
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>category</code> attribute.
     *
     * @return the value of the <code>category</code> attribute.
     */
    public String getCategory()
    {
        return (String)get(categoryDef);
    }
    
    /**
     * Returns the value of the <code>category</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>category</code> attribute.
     */
    public String getCategory(String defaultValue)
    {
        if(isDefined(categoryDef))
        {
            return (String)get(categoryDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>category</code> attribute.
     *
     * @param value the value of the <code>category</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setCategory(String value)
    {
        try
        {
            if(value != null)
            {
                set(categoryDef, value);
            }
            else
            {
                unset(categoryDef);
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
	 * Checks if the value of the <code>category</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>category</code> attribute is defined.
	 */
    public boolean isCategoryDefined()
	{
	    return isDefined(categoryDef);
	}
 
    /**
     * Returns the value of the <code>categoryQuery</code> attribute.
     *
     * @return the value of the <code>categoryQuery</code> attribute.
     */
    public CategoryQueryResource getCategoryQuery()
    {
        return (CategoryQueryResource)get(categoryQueryDef);
    }
    
    /**
     * Returns the value of the <code>categoryQuery</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>categoryQuery</code> attribute.
     */
    public CategoryQueryResource getCategoryQuery(CategoryQueryResource defaultValue)
    {
        if(isDefined(categoryQueryDef))
        {
            return (CategoryQueryResource)get(categoryQueryDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>categoryQuery</code> attribute.
     *
     * @param value the value of the <code>categoryQuery</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setCategoryQuery(CategoryQueryResource value)
    {
        try
        {
            if(value != null)
            {
                set(categoryQueryDef, value);
            }
            else
            {
                unset(categoryQueryDef);
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
	 * Checks if the value of the <code>categoryQuery</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>categoryQuery</code> attribute is defined.
	 */
    public boolean isCategoryQueryDefined()
	{
	    return isDefined(categoryQueryDef);
	}
 
    /**
     * Returns the value of the <code>contents</code> attribute.
     *
     * @return the value of the <code>contents</code> attribute.
     */
    public String getContents()
    {
        return (String)get(contentsDef);
    }
    
    /**
     * Returns the value of the <code>contents</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>contents</code> attribute.
     */
    public String getContents(String defaultValue)
    {
        if(isDefined(contentsDef))
        {
            return (String)get(contentsDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>contents</code> attribute.
     *
     * @param value the value of the <code>contents</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setContents(String value)
    {
        try
        {
            if(value != null)
            {
                set(contentsDef, value);
            }
            else
            {
                unset(contentsDef);
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
	 * Checks if the value of the <code>contents</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>contents</code> attribute is defined.
	 */
    public boolean isContentsDefined()
	{
	    return isDefined(contentsDef);
	}
 
    /**
     * Returns the value of the <code>copyright</code> attribute.
     *
     * @return the value of the <code>copyright</code> attribute.
     */
    public String getCopyright()
    {
        return (String)get(copyrightDef);
    }
    
    /**
     * Returns the value of the <code>copyright</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>copyright</code> attribute.
     */
    public String getCopyright(String defaultValue)
    {
        if(isDefined(copyrightDef))
        {
            return (String)get(copyrightDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>copyright</code> attribute.
     *
     * @param value the value of the <code>copyright</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setCopyright(String value)
    {
        try
        {
            if(value != null)
            {
                set(copyrightDef, value);
            }
            else
            {
                unset(copyrightDef);
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
	 * Checks if the value of the <code>copyright</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>copyright</code> attribute is defined.
	 */
    public boolean isCopyrightDefined()
	{
	    return isDefined(copyrightDef);
	}
 
    /**
     * Returns the value of the <code>generationTemplate</code> attribute.
     *
     * @return the value of the <code>generationTemplate</code> attribute.
     */
    public String getGenerationTemplate()
    {
        return (String)get(generationTemplateDef);
    }
    
    /**
     * Returns the value of the <code>generationTemplate</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>generationTemplate</code> attribute.
     */
    public String getGenerationTemplate(String defaultValue)
    {
        if(isDefined(generationTemplateDef))
        {
            return (String)get(generationTemplateDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>generationTemplate</code> attribute.
     *
     * @param value the value of the <code>generationTemplate</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setGenerationTemplate(String value)
    {
        try
        {
            if(value != null)
            {
                set(generationTemplateDef, value);
            }
            else
            {
                unset(generationTemplateDef);
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
	 * Checks if the value of the <code>generationTemplate</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>generationTemplate</code> attribute is defined.
	 */
    public boolean isGenerationTemplateDefined()
	{
	    return isDefined(generationTemplateDef);
	}

    /**
     * Returns the value of the <code>interval</code> attribute.
     *
     * @return the value of the <code>interval</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public int getInterval()
        throws IllegalStateException
    {
        if(isDefined(intervalDef))
        {
            return ((Integer)get(intervalDef)).intValue();
        }
        else
        {
            throw new IllegalStateException("value of attribute interval is undefined"+
			    " for resource #"+getId());
        }
    }

    /**
     * Returns the value of the <code>interval</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>interval</code> attribute.
     */
    public int getInterval(int defaultValue)
    {
        if(isDefined(intervalDef))
        {
            return ((Integer)get(intervalDef)).intValue();
        }
        else
        {
            return defaultValue;
        }
    }

    /**
     * Sets the value of the <code>interval</code> attribute.
     *
     * @param value the value of the <code>interval</code> attribute.
     */
    public void setInterval(int value)
    {
        try
        {
            set(intervalDef, new Integer(value));
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
     * Removes the value of the <code>interval</code> attribute.
     */
    public void unsetInterval()
    {
        try
        {
            unset(intervalDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>interval</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>interval</code> attribute is defined.
	 */
    public boolean isIntervalDefined()
	{
	    return isDefined(intervalDef);
	}
 
    /**
     * Returns the value of the <code>language</code> attribute.
     *
     * @return the value of the <code>language</code> attribute.
     */
    public String getLanguage()
    {
        return (String)get(languageDef);
    }
    
    /**
     * Returns the value of the <code>language</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>language</code> attribute.
     */
    public String getLanguage(String defaultValue)
    {
        if(isDefined(languageDef))
        {
            return (String)get(languageDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>language</code> attribute.
     *
     * @param value the value of the <code>language</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setLanguage(String value)
    {
        try
        {
            if(value != null)
            {
                set(languageDef, value);
            }
            else
            {
                unset(languageDef);
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
	 * Checks if the value of the <code>language</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>language</code> attribute is defined.
	 */
    public boolean isLanguageDefined()
	{
	    return isDefined(languageDef);
	}
 
    /**
     * Returns the value of the <code>lastUpdate</code> attribute.
     *
     * @return the value of the <code>lastUpdate</code> attribute.
     */
    public Date getLastUpdate()
    {
        return (Date)get(lastUpdateDef);
    }
    
    /**
     * Returns the value of the <code>lastUpdate</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>lastUpdate</code> attribute.
     */
    public Date getLastUpdate(Date defaultValue)
    {
        if(isDefined(lastUpdateDef))
        {
            return (Date)get(lastUpdateDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>lastUpdate</code> attribute.
     *
     * @param value the value of the <code>lastUpdate</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setLastUpdate(Date value)
    {
        try
        {
            if(value != null)
            {
                set(lastUpdateDef, value);
            }
            else
            {
                unset(lastUpdateDef);
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
	 * Checks if the value of the <code>lastUpdate</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>lastUpdate</code> attribute is defined.
	 */
    public boolean isLastUpdateDefined()
	{
	    return isDefined(lastUpdateDef);
	}
 
    /**
     * Returns the value of the <code>managingEditor</code> attribute.
     *
     * @return the value of the <code>managingEditor</code> attribute.
     */
    public String getManagingEditor()
    {
        return (String)get(managingEditorDef);
    }
    
    /**
     * Returns the value of the <code>managingEditor</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>managingEditor</code> attribute.
     */
    public String getManagingEditor(String defaultValue)
    {
        if(isDefined(managingEditorDef))
        {
            return (String)get(managingEditorDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>managingEditor</code> attribute.
     *
     * @param value the value of the <code>managingEditor</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setManagingEditor(String value)
    {
        try
        {
            if(value != null)
            {
                set(managingEditorDef, value);
            }
            else
            {
                unset(managingEditorDef);
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
	 * Checks if the value of the <code>managingEditor</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>managingEditor</code> attribute is defined.
	 */
    public boolean isManagingEditorDefined()
	{
	    return isDefined(managingEditorDef);
	}

    /**
     * Returns the value of the <code>public</code> attribute.
     *
     * @return the value of the <code>public</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getPublic()
        throws IllegalStateException
    {
        if(isDefined(publicDef))
        {
            return ((Boolean)get(publicDef)).booleanValue();
        }
        else
        {
            throw new IllegalStateException("value of attribute public is undefined"+
			    " for resource #"+getId());
        }
    }

    /**
     * Returns the value of the <code>public</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>public</code> attribute.
     */
    public boolean getPublic(boolean defaultValue)
    {
        if(isDefined(publicDef))
        {
            return ((Boolean)get(publicDef)).booleanValue();
        }
        else
        {
            return defaultValue;
        }
    }

    /**
     * Sets the value of the <code>public</code> attribute.
     *
     * @param value the value of the <code>public</code> attribute.
     */
    public void setPublic(boolean value)
    {
        try
        {
            set(publicDef, new Boolean(value));
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
     * Removes the value of the <code>public</code> attribute.
     */
    public void unsetPublic()
    {
        try
        {
            unset(publicDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>public</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>public</code> attribute is defined.
	 */
    public boolean isPublicDefined()
	{
	    return isDefined(publicDef);
	}
 
    /**
     * Returns the value of the <code>webMaster</code> attribute.
     *
     * @return the value of the <code>webMaster</code> attribute.
     */
    public String getWebMaster()
    {
        return (String)get(webMasterDef);
    }
    
    /**
     * Returns the value of the <code>webMaster</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>webMaster</code> attribute.
     */
    public String getWebMaster(String defaultValue)
    {
        if(isDefined(webMasterDef))
        {
            return (String)get(webMasterDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>webMaster</code> attribute.
     *
     * @param value the value of the <code>webMaster</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setWebMaster(String value)
    {
        try
        {
            if(value != null)
            {
                set(webMasterDef, value);
            }
            else
            {
                unset(webMasterDef);
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
	 * Checks if the value of the <code>webMaster</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>webMaster</code> attribute is defined.
	 */
    public boolean isWebMasterDefined()
	{
	    return isDefined(webMasterDef);
	}
  
    // @custom methods ///////////////////////////////////////////////////////
}
