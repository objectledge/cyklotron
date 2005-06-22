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
 * An implementation of <code>integration.resource_class</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class ResourceClassResourceImpl
    extends CmsNodeResourceImpl
    implements ResourceClassResource
{
    // class variables /////////////////////////////////////////////////////////

    /** Class variables initialization status. */
    private static boolean definitionsInitialized;
	
    /** The AttributeDefinition object for the <code>aggregationCopyAction</code> attribute. */
    private static AttributeDefinition aggregationCopyActionDef;

    /** The AttributeDefinition object for the <code>aggregationParentClasses</code> attribute. */
    private static AttributeDefinition aggregationParentClassesDef;

    /** The AttributeDefinition object for the <code>aggregationTargetPaths</code> attribute. */
    private static AttributeDefinition aggregationTargetPathsDef;

    /** The AttributeDefinition object for the <code>aggregationUpdateAction</code> attribute. */
    private static AttributeDefinition aggregationUpdateActionDef;

    /** The AttributeDefinition object for the <code>categorizable</code> attribute. */
    private static AttributeDefinition categorizableDef;

    /** The AttributeDefinition object for the <code>editView</code> attribute. */
    private static AttributeDefinition editViewDef;

    /** The AttributeDefinition object for the <code>image</code> attribute. */
    private static AttributeDefinition imageDef;

    /** The AttributeDefinition object for the <code>indexDescription</code> attribute. */
    private static AttributeDefinition indexDescriptionDef;

    /** The AttributeDefinition object for the <code>indexTitle</code> attribute. */
    private static AttributeDefinition indexTitleDef;

    /** The AttributeDefinition object for the <code>indexableFields</code> attribute. */
    private static AttributeDefinition indexableFieldsDef;

    /** The AttributeDefinition object for the <code>relatedQuickAddView</code> attribute. */
    private static AttributeDefinition relatedQuickAddViewDef;

    /** The AttributeDefinition object for the <code>relatedSupported</code> attribute. */
    private static AttributeDefinition relatedSupportedDef;

    /** The AttributeDefinition object for the <code>view</code> attribute. */
    private static AttributeDefinition viewDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>integration.resource_class</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     */
    public ResourceClassResourceImpl()
    {
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
     * @throws InvalidResourceNameException if the name argument contains illegal characters.
     */
    public static ResourceClassResource createResourceClassResource(CoralSession session, String
        name, Resource parent)
        throws InvalidResourceNameException
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
     * Returns the value of the <code>aggregationCopyAction</code> attribute.
     *
     * @return the value of the <code>aggregationCopyAction</code> attribute.
     */
    public String getAggregationCopyAction()
    {
        return (String)get(aggregationCopyActionDef);
    }
    
    /**
     * Returns the value of the <code>aggregationCopyAction</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>aggregationCopyAction</code> attribute.
     */
    public String getAggregationCopyAction(String defaultValue)
    {
        if(isDefined(aggregationCopyActionDef))
        {
            return (String)get(aggregationCopyActionDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>aggregationCopyAction</code> attribute.
     *
     * @param value the value of the <code>aggregationCopyAction</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAggregationCopyAction(String value)
    {
        try
        {
            if(value != null)
            {
                set(aggregationCopyActionDef, value);
            }
            else
            {
                unset(aggregationCopyActionDef);
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
	 * Checks if the value of the <code>aggregationCopyAction</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>aggregationCopyAction</code> attribute is defined.
	 */
    public boolean isAggregationCopyActionDefined()
	{
	    return isDefined(aggregationCopyActionDef);
	}
 
    /**
     * Returns the value of the <code>aggregationParentClasses</code> attribute.
     *
     * @return the value of the <code>aggregationParentClasses</code> attribute.
     */
    public String getAggregationParentClasses()
    {
        return (String)get(aggregationParentClassesDef);
    }
    
    /**
     * Returns the value of the <code>aggregationParentClasses</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>aggregationParentClasses</code> attribute.
     */
    public String getAggregationParentClasses(String defaultValue)
    {
        if(isDefined(aggregationParentClassesDef))
        {
            return (String)get(aggregationParentClassesDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>aggregationParentClasses</code> attribute.
     *
     * @param value the value of the <code>aggregationParentClasses</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAggregationParentClasses(String value)
    {
        try
        {
            if(value != null)
            {
                set(aggregationParentClassesDef, value);
            }
            else
            {
                unset(aggregationParentClassesDef);
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
	 * Checks if the value of the <code>aggregationParentClasses</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>aggregationParentClasses</code> attribute is defined.
	 */
    public boolean isAggregationParentClassesDefined()
	{
	    return isDefined(aggregationParentClassesDef);
	}
 
    /**
     * Returns the value of the <code>aggregationTargetPaths</code> attribute.
     *
     * @return the value of the <code>aggregationTargetPaths</code> attribute.
     */
    public String getAggregationTargetPaths()
    {
        return (String)get(aggregationTargetPathsDef);
    }
    
    /**
     * Returns the value of the <code>aggregationTargetPaths</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>aggregationTargetPaths</code> attribute.
     */
    public String getAggregationTargetPaths(String defaultValue)
    {
        if(isDefined(aggregationTargetPathsDef))
        {
            return (String)get(aggregationTargetPathsDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>aggregationTargetPaths</code> attribute.
     *
     * @param value the value of the <code>aggregationTargetPaths</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAggregationTargetPaths(String value)
    {
        try
        {
            if(value != null)
            {
                set(aggregationTargetPathsDef, value);
            }
            else
            {
                unset(aggregationTargetPathsDef);
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
	 * Checks if the value of the <code>aggregationTargetPaths</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>aggregationTargetPaths</code> attribute is defined.
	 */
    public boolean isAggregationTargetPathsDefined()
	{
	    return isDefined(aggregationTargetPathsDef);
	}
 
    /**
     * Returns the value of the <code>aggregationUpdateAction</code> attribute.
     *
     * @return the value of the <code>aggregationUpdateAction</code> attribute.
     */
    public String getAggregationUpdateAction()
    {
        return (String)get(aggregationUpdateActionDef);
    }
    
    /**
     * Returns the value of the <code>aggregationUpdateAction</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>aggregationUpdateAction</code> attribute.
     */
    public String getAggregationUpdateAction(String defaultValue)
    {
        if(isDefined(aggregationUpdateActionDef))
        {
            return (String)get(aggregationUpdateActionDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>aggregationUpdateAction</code> attribute.
     *
     * @param value the value of the <code>aggregationUpdateAction</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAggregationUpdateAction(String value)
    {
        try
        {
            if(value != null)
            {
                set(aggregationUpdateActionDef, value);
            }
            else
            {
                unset(aggregationUpdateActionDef);
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
	 * Checks if the value of the <code>aggregationUpdateAction</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>aggregationUpdateAction</code> attribute is defined.
	 */
    public boolean isAggregationUpdateActionDefined()
	{
	    return isDefined(aggregationUpdateActionDef);
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
            throw new IllegalStateException("value of attribute categorizable is undefined"+
			    " for resource #"+getId());
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
     * Returns the value of the <code>editView</code> attribute.
     *
     * @return the value of the <code>editView</code> attribute.
     */
    public String getEditView()
    {
        return (String)get(editViewDef);
    }
    
    /**
     * Returns the value of the <code>editView</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>editView</code> attribute.
     */
    public String getEditView(String defaultValue)
    {
        if(isDefined(editViewDef))
        {
            return (String)get(editViewDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>editView</code> attribute.
     *
     * @param value the value of the <code>editView</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setEditView(String value)
    {
        try
        {
            if(value != null)
            {
                set(editViewDef, value);
            }
            else
            {
                unset(editViewDef);
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
	 * Checks if the value of the <code>editView</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>editView</code> attribute is defined.
	 */
    public boolean isEditViewDefined()
	{
	    return isDefined(editViewDef);
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
     * Returns the value of the <code>indexDescription</code> attribute.
     *
     * @return the value of the <code>indexDescription</code> attribute.
     */
    public String getIndexDescription()
    {
        return (String)get(indexDescriptionDef);
    }
    
    /**
     * Returns the value of the <code>indexDescription</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>indexDescription</code> attribute.
     */
    public String getIndexDescription(String defaultValue)
    {
        if(isDefined(indexDescriptionDef))
        {
            return (String)get(indexDescriptionDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>indexDescription</code> attribute.
     *
     * @param value the value of the <code>indexDescription</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setIndexDescription(String value)
    {
        try
        {
            if(value != null)
            {
                set(indexDescriptionDef, value);
            }
            else
            {
                unset(indexDescriptionDef);
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
	 * Checks if the value of the <code>indexDescription</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>indexDescription</code> attribute is defined.
	 */
    public boolean isIndexDescriptionDefined()
	{
	    return isDefined(indexDescriptionDef);
	}
 
    /**
     * Returns the value of the <code>indexTitle</code> attribute.
     *
     * @return the value of the <code>indexTitle</code> attribute.
     */
    public String getIndexTitle()
    {
        return (String)get(indexTitleDef);
    }
    
    /**
     * Returns the value of the <code>indexTitle</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>indexTitle</code> attribute.
     */
    public String getIndexTitle(String defaultValue)
    {
        if(isDefined(indexTitleDef))
        {
            return (String)get(indexTitleDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>indexTitle</code> attribute.
     *
     * @param value the value of the <code>indexTitle</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setIndexTitle(String value)
    {
        try
        {
            if(value != null)
            {
                set(indexTitleDef, value);
            }
            else
            {
                unset(indexTitleDef);
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
	 * Checks if the value of the <code>indexTitle</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>indexTitle</code> attribute is defined.
	 */
    public boolean isIndexTitleDefined()
	{
	    return isDefined(indexTitleDef);
	}
 
    /**
     * Returns the value of the <code>indexableFields</code> attribute.
     *
     * @return the value of the <code>indexableFields</code> attribute.
     */
    public String getIndexableFields()
    {
        return (String)get(indexableFieldsDef);
    }
    
    /**
     * Returns the value of the <code>indexableFields</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>indexableFields</code> attribute.
     */
    public String getIndexableFields(String defaultValue)
    {
        if(isDefined(indexableFieldsDef))
        {
            return (String)get(indexableFieldsDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>indexableFields</code> attribute.
     *
     * @param value the value of the <code>indexableFields</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setIndexableFields(String value)
    {
        try
        {
            if(value != null)
            {
                set(indexableFieldsDef, value);
            }
            else
            {
                unset(indexableFieldsDef);
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
	 * Checks if the value of the <code>indexableFields</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>indexableFields</code> attribute is defined.
	 */
    public boolean isIndexableFieldsDefined()
	{
	    return isDefined(indexableFieldsDef);
	}
 
    /**
     * Returns the value of the <code>relatedQuickAddView</code> attribute.
     *
     * @return the value of the <code>relatedQuickAddView</code> attribute.
     */
    public String getRelatedQuickAddView()
    {
        return (String)get(relatedQuickAddViewDef);
    }
    
    /**
     * Returns the value of the <code>relatedQuickAddView</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>relatedQuickAddView</code> attribute.
     */
    public String getRelatedQuickAddView(String defaultValue)
    {
        if(isDefined(relatedQuickAddViewDef))
        {
            return (String)get(relatedQuickAddViewDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>relatedQuickAddView</code> attribute.
     *
     * @param value the value of the <code>relatedQuickAddView</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setRelatedQuickAddView(String value)
    {
        try
        {
            if(value != null)
            {
                set(relatedQuickAddViewDef, value);
            }
            else
            {
                unset(relatedQuickAddViewDef);
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
	 * Checks if the value of the <code>relatedQuickAddView</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>relatedQuickAddView</code> attribute is defined.
	 */
    public boolean isRelatedQuickAddViewDefined()
	{
	    return isDefined(relatedQuickAddViewDef);
	}

    /**
     * Returns the value of the <code>relatedSupported</code> attribute.
     *
     * @return the value of the <code>relatedSupported</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getRelatedSupported()
        throws IllegalStateException
    {
        if(isDefined(relatedSupportedDef))
        {
            return ((Boolean)get(relatedSupportedDef)).booleanValue();
        }
        else
        {
            throw new IllegalStateException("value of attribute relatedSupported is undefined"+
			    " for resource #"+getId());
        }
    }

    /**
     * Returns the value of the <code>relatedSupported</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>relatedSupported</code> attribute.
     */
    public boolean getRelatedSupported(boolean defaultValue)
    {
        if(isDefined(relatedSupportedDef))
        {
            return ((Boolean)get(relatedSupportedDef)).booleanValue();
        }
        else
        {
            return defaultValue;
        }
    }

    /**
     * Sets the value of the <code>relatedSupported</code> attribute.
     *
     * @param value the value of the <code>relatedSupported</code> attribute.
     */
    public void setRelatedSupported(boolean value)
    {
        try
        {
            set(relatedSupportedDef, new Boolean(value));
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
     * Removes the value of the <code>relatedSupported</code> attribute.
     */
    public void unsetRelatedSupported()
    {
        try
        {
            unset(relatedSupportedDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>relatedSupported</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>relatedSupported</code> attribute is defined.
	 */
    public boolean isRelatedSupportedDefined()
	{
	    return isDefined(relatedSupportedDef);
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
