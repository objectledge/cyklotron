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
 
package net.cyklotron.cms.integration;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.objectledge.coral.BackendException;
import org.objectledge.coral.datatypes.NodeImpl;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.CoralSchema;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.ModificationNotPermitedException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;
import org.objectledge.database.Database;

import org.jcontainer.dna.Logger;

/**
 * An implementation of <code>integration.resource_class</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class ResourceClassResourceImpl
    extends NodeImpl
    implements ResourceClassResource
{
    // instance variables ////////////////////////////////////////////////////

    /** The AttributeDefinition object for the <code>aggregation_copy_action</code> attribute. */
    private AttributeDefinition aggregation_copy_actionDef;

    /** The AttributeDefinition object for the <code>aggregation_parent_classes</code> attribute. */
    private AttributeDefinition aggregation_parent_classesDef;

    /** The AttributeDefinition object for the <code>aggregation_target_paths</code> attribute. */
    private AttributeDefinition aggregation_target_pathsDef;

    /** The AttributeDefinition object for the <code>aggregation_update_action</code> attribute. */
    private AttributeDefinition aggregation_update_actionDef;

    /** The AttributeDefinition object for the <code>categorizable</code> attribute. */
    private AttributeDefinition categorizableDef;

    /** The AttributeDefinition object for the <code>image</code> attribute. */
    private AttributeDefinition imageDef;

    /** The AttributeDefinition object for the <code>index_description</code> attribute. */
    private AttributeDefinition index_descriptionDef;

    /** The AttributeDefinition object for the <code>index_title</code> attribute. */
    private AttributeDefinition index_titleDef;

    /** The AttributeDefinition object for the <code>indexable_fields</code> attribute. */
    private AttributeDefinition indexable_fieldsDef;

    /** The AttributeDefinition object for the <code>related_quick_add_view</code> attribute. */
    private AttributeDefinition related_quick_add_viewDef;

    /** The AttributeDefinition object for the <code>related_supported</code> attribute. */
    private AttributeDefinition related_supportedDef;

    /** The AttributeDefinition object for the <code>view</code> attribute. */
    private AttributeDefinition viewDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>integration.resource_class</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     * @param schema the CoralSchema.
     * @param database the Database.
     * @param logger the Logger.
     */
    public ResourceClassResourceImpl(CoralSchema schema, Database database, Logger logger)
    {
        super(schema, database, logger);
        try
        {
            ResourceClass rc = schema.getResourceClass("integration.resource_class");
            aggregation_copy_actionDef = rc.getAttribute("aggregation_copy_action");
            aggregation_parent_classesDef = rc.getAttribute("aggregation_parent_classes");
            aggregation_target_pathsDef = rc.getAttribute("aggregation_target_paths");
            aggregation_update_actionDef = rc.getAttribute("aggregation_update_action");
            categorizableDef = rc.getAttribute("categorizable");
            imageDef = rc.getAttribute("image");
            index_descriptionDef = rc.getAttribute("index_description");
            index_titleDef = rc.getAttribute("index_title");
            indexable_fieldsDef = rc.getAttribute("indexable_fields");
            related_quick_add_viewDef = rc.getAttribute("related_quick_add_view");
            related_supportedDef = rc.getAttribute("related_supported");
            viewDef = rc.getAttribute("view");
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>integration.resource_class</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static ResourceClassResource getResourceClassResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof ResourceClassResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not integration.resource_class");
        }
        return (ResourceClassResource)res;
    }

    /**
     * Creates a new <code>integration.resource_class</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @return a new ResourceClassResource instance.
     */
    public static ResourceClassResource createResourceClassResource(CoralSession session, String
        name, Resource parent)
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("integration.resource_class");
            Map attrs = new HashMap();
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof ResourceClassResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (ResourceClassResource)res;
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
     * Returns the value of the <code>aggregation_copy_action</code> attribute.
     *
     * @return the value of the <code>aggregation_copy_action</code> attribute.
     */
    public String getAggregation_copy_action()
    {
        return (String)get(aggregation_copy_actionDef);
    }
    
    /**
     * Returns the value of the <code>aggregation_copy_action</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>aggregation_copy_action</code> attribute.
     */
    public String getAggregation_copy_action(String defaultValue)
    {
        if(isDefined(aggregation_copy_actionDef))
        {
            return (String)get(aggregation_copy_actionDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>aggregation_copy_action</code> attribute.
     *
     * @param value the value of the <code>aggregation_copy_action</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAggregation_copy_action(String value)
    {
        try
        {
            if(value != null)
            {
                set(aggregation_copy_actionDef, value);
            }
            else
            {
                unset(aggregation_copy_actionDef);
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
	 * Checks if the value of the <code>aggregation_copy_action</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>aggregation_copy_action</code> attribute is defined.
	 */
    public boolean isAggregation_copy_actionDefined()
	{
	    return isDefined(aggregation_copy_actionDef);
	}
 
    /**
     * Returns the value of the <code>aggregation_parent_classes</code> attribute.
     *
     * @return the value of the <code>aggregation_parent_classes</code> attribute.
     */
    public String getAggregation_parent_classes()
    {
        return (String)get(aggregation_parent_classesDef);
    }
    
    /**
     * Returns the value of the <code>aggregation_parent_classes</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>aggregation_parent_classes</code> attribute.
     */
    public String getAggregation_parent_classes(String defaultValue)
    {
        if(isDefined(aggregation_parent_classesDef))
        {
            return (String)get(aggregation_parent_classesDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>aggregation_parent_classes</code> attribute.
     *
     * @param value the value of the <code>aggregation_parent_classes</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAggregation_parent_classes(String value)
    {
        try
        {
            if(value != null)
            {
                set(aggregation_parent_classesDef, value);
            }
            else
            {
                unset(aggregation_parent_classesDef);
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
	 * Checks if the value of the <code>aggregation_parent_classes</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>aggregation_parent_classes</code> attribute is defined.
	 */
    public boolean isAggregation_parent_classesDefined()
	{
	    return isDefined(aggregation_parent_classesDef);
	}
 
    /**
     * Returns the value of the <code>aggregation_target_paths</code> attribute.
     *
     * @return the value of the <code>aggregation_target_paths</code> attribute.
     */
    public String getAggregation_target_paths()
    {
        return (String)get(aggregation_target_pathsDef);
    }
    
    /**
     * Returns the value of the <code>aggregation_target_paths</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>aggregation_target_paths</code> attribute.
     */
    public String getAggregation_target_paths(String defaultValue)
    {
        if(isDefined(aggregation_target_pathsDef))
        {
            return (String)get(aggregation_target_pathsDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>aggregation_target_paths</code> attribute.
     *
     * @param value the value of the <code>aggregation_target_paths</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAggregation_target_paths(String value)
    {
        try
        {
            if(value != null)
            {
                set(aggregation_target_pathsDef, value);
            }
            else
            {
                unset(aggregation_target_pathsDef);
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
	 * Checks if the value of the <code>aggregation_target_paths</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>aggregation_target_paths</code> attribute is defined.
	 */
    public boolean isAggregation_target_pathsDefined()
	{
	    return isDefined(aggregation_target_pathsDef);
	}
 
    /**
     * Returns the value of the <code>aggregation_update_action</code> attribute.
     *
     * @return the value of the <code>aggregation_update_action</code> attribute.
     */
    public String getAggregation_update_action()
    {
        return (String)get(aggregation_update_actionDef);
    }
    
    /**
     * Returns the value of the <code>aggregation_update_action</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>aggregation_update_action</code> attribute.
     */
    public String getAggregation_update_action(String defaultValue)
    {
        if(isDefined(aggregation_update_actionDef))
        {
            return (String)get(aggregation_update_actionDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>aggregation_update_action</code> attribute.
     *
     * @param value the value of the <code>aggregation_update_action</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAggregation_update_action(String value)
    {
        try
        {
            if(value != null)
            {
                set(aggregation_update_actionDef, value);
            }
            else
            {
                unset(aggregation_update_actionDef);
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
	 * Checks if the value of the <code>aggregation_update_action</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>aggregation_update_action</code> attribute is defined.
	 */
    public boolean isAggregation_update_actionDefined()
	{
	    return isDefined(aggregation_update_actionDef);
	}

    /**
     * Returns the value of the <code>categorizable</code> attribute.
     *
     * @return the value of the <code>categorizable</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getCategorizable()
        throws IllegalStateException
    {
        if(isDefined(categorizableDef))
        {
            return ((Boolean)get(categorizableDef)).booleanValue();
        }
        else
        {
            throw new IllegalStateException("attribute value is undefined");
        }
    }

    /**
     * Returns the value of the <code>categorizable</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>categorizable</code> attribute.
     */
    public boolean getCategorizable(boolean defaultValue)
    {
        if(isDefined(categorizableDef))
        {
            return ((Boolean)get(categorizableDef)).booleanValue();
        }
        else
        {
            return defaultValue;
        }
    }

    /**
     * Sets the value of the <code>categorizable</code> attribute.
     *
     * @param value the value of the <code>categorizable</code> attribute.
     */
    public void setCategorizable(boolean value)
    {
        try
        {
            set(categorizableDef, new Boolean(value));
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
     * Removes the value of the <code>categorizable</code> attribute.
     */
    public void unsetCategorizable()
    {
        try
        {
            unset(categorizableDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>categorizable</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>categorizable</code> attribute is defined.
	 */
    public boolean isCategorizableDefined()
	{
	    return isDefined(categorizableDef);
	}
 
    /**
     * Returns the value of the <code>image</code> attribute.
     *
     * @return the value of the <code>image</code> attribute.
     */
    public String getImage()
    {
        return (String)get(imageDef);
    }
    
    /**
     * Returns the value of the <code>image</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>image</code> attribute.
     */
    public String getImage(String defaultValue)
    {
        if(isDefined(imageDef))
        {
            return (String)get(imageDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>image</code> attribute.
     *
     * @param value the value of the <code>image</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setImage(String value)
    {
        try
        {
            if(value != null)
            {
                set(imageDef, value);
            }
            else
            {
                unset(imageDef);
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
	 * Checks if the value of the <code>image</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>image</code> attribute is defined.
	 */
    public boolean isImageDefined()
	{
	    return isDefined(imageDef);
	}
 
    /**
     * Returns the value of the <code>index_description</code> attribute.
     *
     * @return the value of the <code>index_description</code> attribute.
     */
    public String getIndex_description()
    {
        return (String)get(index_descriptionDef);
    }
    
    /**
     * Returns the value of the <code>index_description</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>index_description</code> attribute.
     */
    public String getIndex_description(String defaultValue)
    {
        if(isDefined(index_descriptionDef))
        {
            return (String)get(index_descriptionDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>index_description</code> attribute.
     *
     * @param value the value of the <code>index_description</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setIndex_description(String value)
    {
        try
        {
            if(value != null)
            {
                set(index_descriptionDef, value);
            }
            else
            {
                unset(index_descriptionDef);
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
	 * Checks if the value of the <code>index_description</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>index_description</code> attribute is defined.
	 */
    public boolean isIndex_descriptionDefined()
	{
	    return isDefined(index_descriptionDef);
	}
 
    /**
     * Returns the value of the <code>index_title</code> attribute.
     *
     * @return the value of the <code>index_title</code> attribute.
     */
    public String getIndex_title()
    {
        return (String)get(index_titleDef);
    }
    
    /**
     * Returns the value of the <code>index_title</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>index_title</code> attribute.
     */
    public String getIndex_title(String defaultValue)
    {
        if(isDefined(index_titleDef))
        {
            return (String)get(index_titleDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>index_title</code> attribute.
     *
     * @param value the value of the <code>index_title</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setIndex_title(String value)
    {
        try
        {
            if(value != null)
            {
                set(index_titleDef, value);
            }
            else
            {
                unset(index_titleDef);
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
	 * Checks if the value of the <code>index_title</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>index_title</code> attribute is defined.
	 */
    public boolean isIndex_titleDefined()
	{
	    return isDefined(index_titleDef);
	}
 
    /**
     * Returns the value of the <code>indexable_fields</code> attribute.
     *
     * @return the value of the <code>indexable_fields</code> attribute.
     */
    public String getIndexable_fields()
    {
        return (String)get(indexable_fieldsDef);
    }
    
    /**
     * Returns the value of the <code>indexable_fields</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>indexable_fields</code> attribute.
     */
    public String getIndexable_fields(String defaultValue)
    {
        if(isDefined(indexable_fieldsDef))
        {
            return (String)get(indexable_fieldsDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>indexable_fields</code> attribute.
     *
     * @param value the value of the <code>indexable_fields</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setIndexable_fields(String value)
    {
        try
        {
            if(value != null)
            {
                set(indexable_fieldsDef, value);
            }
            else
            {
                unset(indexable_fieldsDef);
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
	 * Checks if the value of the <code>indexable_fields</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>indexable_fields</code> attribute is defined.
	 */
    public boolean isIndexable_fieldsDefined()
	{
	    return isDefined(indexable_fieldsDef);
	}
 
    /**
     * Returns the value of the <code>related_quick_add_view</code> attribute.
     *
     * @return the value of the <code>related_quick_add_view</code> attribute.
     */
    public String getRelated_quick_add_view()
    {
        return (String)get(related_quick_add_viewDef);
    }
    
    /**
     * Returns the value of the <code>related_quick_add_view</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>related_quick_add_view</code> attribute.
     */
    public String getRelated_quick_add_view(String defaultValue)
    {
        if(isDefined(related_quick_add_viewDef))
        {
            return (String)get(related_quick_add_viewDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>related_quick_add_view</code> attribute.
     *
     * @param value the value of the <code>related_quick_add_view</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setRelated_quick_add_view(String value)
    {
        try
        {
            if(value != null)
            {
                set(related_quick_add_viewDef, value);
            }
            else
            {
                unset(related_quick_add_viewDef);
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
	 * Checks if the value of the <code>related_quick_add_view</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>related_quick_add_view</code> attribute is defined.
	 */
    public boolean isRelated_quick_add_viewDefined()
	{
	    return isDefined(related_quick_add_viewDef);
	}

    /**
     * Returns the value of the <code>related_supported</code> attribute.
     *
     * @return the value of the <code>related_supported</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getRelated_supported()
        throws IllegalStateException
    {
        if(isDefined(related_supportedDef))
        {
            return ((Boolean)get(related_supportedDef)).booleanValue();
        }
        else
        {
            throw new IllegalStateException("attribute value is undefined");
        }
    }

    /**
     * Returns the value of the <code>related_supported</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>related_supported</code> attribute.
     */
    public boolean getRelated_supported(boolean defaultValue)
    {
        if(isDefined(related_supportedDef))
        {
            return ((Boolean)get(related_supportedDef)).booleanValue();
        }
        else
        {
            return defaultValue;
        }
    }

    /**
     * Sets the value of the <code>related_supported</code> attribute.
     *
     * @param value the value of the <code>related_supported</code> attribute.
     */
    public void setRelated_supported(boolean value)
    {
        try
        {
            set(related_supportedDef, new Boolean(value));
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
     * Removes the value of the <code>related_supported</code> attribute.
     */
    public void unsetRelated_supported()
    {
        try
        {
            unset(related_supportedDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>related_supported</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>related_supported</code> attribute is defined.
	 */
    public boolean isRelated_supportedDefined()
	{
	    return isDefined(related_supportedDef);
	}
 
    /**
     * Returns the value of the <code>view</code> attribute.
     *
     * @return the value of the <code>view</code> attribute.
     */
    public String getView()
    {
        return (String)get(viewDef);
    }
    
    /**
     * Returns the value of the <code>view</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>view</code> attribute.
     */
    public String getView(String defaultValue)
    {
        if(isDefined(viewDef))
        {
            return (String)get(viewDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>view</code> attribute.
     *
     * @param value the value of the <code>view</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setView(String value)
    {
        try
        {
            if(value != null)
            {
                set(viewDef, value);
            }
            else
            {
                unset(viewDef);
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
	 * Checks if the value of the <code>view</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>view</code> attribute is defined.
	 */
    public boolean isViewDefined()
	{
	    return isDefined(viewDef);
	}
  
    // @custom methods ///////////////////////////////////////////////////////
    // @import java.util.StringTokenizer

    public String[] getAggregationParentClassesList()
    {
        return tokenize(getAggregationParentClasses(), ",");
    }
    
    public String[] getAggregationTargetPathsList()
    {
        return tokenize(getAggregationTargetPaths(), ",");
    }

    private String[] tokenize(String string, String delimiter)
    {
        if(string == null || string.length() == 0)
        {
            return new String[0];
        }
        StringTokenizer st = new StringTokenizer(string, delimiter, false);
        int size = st.countTokens();
        String[] result = new String[size];
        for(int i = 0; i < size; i++)
        {
            result[i] = st.nextToken();
        }
        return result;
    }
}
