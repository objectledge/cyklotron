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
	@SuppressWarnings("unused")
    private static boolean definitionsInitialized;
	
    /** The AttributeDefinition object for the <code>aggregationCopyAction</code> attribute. */
	private static AttributeDefinition<String> aggregationCopyActionDef;

    /** The AttributeDefinition object for the <code>aggregationParentClasses</code> attribute. */
	private static AttributeDefinition<String> aggregationParentClassesDef;

    /** The AttributeDefinition object for the <code>aggregationRecursiveCopyAction</code> attribute. */
	private static AttributeDefinition<String> aggregationRecursiveCopyActionDef;

    /** The AttributeDefinition object for the <code>aggregationRecursiveUpdateAction</code> attribute. */
	private static AttributeDefinition<String> aggregationRecursiveUpdateActionDef;

    /** The AttributeDefinition object for the <code>aggregationTargetPaths</code> attribute. */
	private static AttributeDefinition<String> aggregationTargetPathsDef;

    /** The AttributeDefinition object for the <code>aggregationUpdateAction</code> attribute. */
	private static AttributeDefinition<String> aggregationUpdateActionDef;

    /** The AttributeDefinition object for the <code>categorizable</code> attribute. */
    private static AttributeDefinition<Boolean> categorizableDef;

    /** The AttributeDefinition object for the <code>editView</code> attribute. */
	private static AttributeDefinition<String> editViewDef;

    /** The AttributeDefinition object for the <code>image</code> attribute. */
	private static AttributeDefinition<String> imageDef;

    /** The AttributeDefinition object for the <code>indexDescription</code> attribute. */
	private static AttributeDefinition<String> indexDescriptionDef;

    /** The AttributeDefinition object for the <code>indexTitle</code> attribute. */
	private static AttributeDefinition<String> indexTitleDef;

    /** The AttributeDefinition object for the <code>indexableFields</code> attribute. */
	private static AttributeDefinition<String> indexableFieldsDef;

    /** The AttributeDefinition object for the <code>pickerSupported</code> attribute. */
    private static AttributeDefinition<Boolean> pickerSupportedDef;

    /** The AttributeDefinition object for the <code>relatedQuickAddView</code> attribute. */
	private static AttributeDefinition<String> relatedQuickAddViewDef;

    /** The AttributeDefinition object for the <code>relatedQuickEditView</code> attribute. */
	private static AttributeDefinition<String> relatedQuickEditViewDef;

    /** The AttributeDefinition object for the <code>relatedSupported</code> attribute. */
    private static AttributeDefinition<Boolean> relatedSupportedDef;

    /** The AttributeDefinition object for the <code>view</code> attribute. */
	private static AttributeDefinition<String> viewDef;

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
            ResourceClass<ResourceClassResource> rc = session.getSchema().getResourceClass("integration.resource_class", ResourceClassResource.class);
		    Resource res = session.getStore().createResource(name, parent, rc,
                java.util.Collections.<AttributeDefinition<?>, Object> emptyMap());			
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
        return get(aggregationCopyActionDef);
    }
    
    /**
     * Returns the value of the <code>aggregationCopyAction</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>aggregationCopyAction</code> attribute.
     */
    public String getAggregationCopyAction(String defaultValue)
    {
        return get(aggregationCopyActionDef, defaultValue);
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
        return get(aggregationParentClassesDef);
    }
    
    /**
     * Returns the value of the <code>aggregationParentClasses</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>aggregationParentClasses</code> attribute.
     */
    public String getAggregationParentClasses(String defaultValue)
    {
        return get(aggregationParentClassesDef, defaultValue);
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
     * Returns the value of the <code>aggregationRecursiveCopyAction</code> attribute.
     *
     * @return the value of the <code>aggregationRecursiveCopyAction</code> attribute.
     */
    public String getAggregationRecursiveCopyAction()
    {
        return get(aggregationRecursiveCopyActionDef);
    }
    
    /**
     * Returns the value of the <code>aggregationRecursiveCopyAction</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>aggregationRecursiveCopyAction</code> attribute.
     */
    public String getAggregationRecursiveCopyAction(String defaultValue)
    {
        return get(aggregationRecursiveCopyActionDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>aggregationRecursiveCopyAction</code> attribute.
     *
     * @param value the value of the <code>aggregationRecursiveCopyAction</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAggregationRecursiveCopyAction(String value)
    {
        try
        {
            if(value != null)
            {
                set(aggregationRecursiveCopyActionDef, value);
            }
            else
            {
                unset(aggregationRecursiveCopyActionDef);
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
	 * Checks if the value of the <code>aggregationRecursiveCopyAction</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>aggregationRecursiveCopyAction</code> attribute is defined.
	 */
    public boolean isAggregationRecursiveCopyActionDefined()
	{
	    return isDefined(aggregationRecursiveCopyActionDef);
	}
 
    /**
     * Returns the value of the <code>aggregationRecursiveUpdateAction</code> attribute.
     *
     * @return the value of the <code>aggregationRecursiveUpdateAction</code> attribute.
     */
    public String getAggregationRecursiveUpdateAction()
    {
        return get(aggregationRecursiveUpdateActionDef);
    }
    
    /**
     * Returns the value of the <code>aggregationRecursiveUpdateAction</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>aggregationRecursiveUpdateAction</code> attribute.
     */
    public String getAggregationRecursiveUpdateAction(String defaultValue)
    {
        return get(aggregationRecursiveUpdateActionDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>aggregationRecursiveUpdateAction</code> attribute.
     *
     * @param value the value of the <code>aggregationRecursiveUpdateAction</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAggregationRecursiveUpdateAction(String value)
    {
        try
        {
            if(value != null)
            {
                set(aggregationRecursiveUpdateActionDef, value);
            }
            else
            {
                unset(aggregationRecursiveUpdateActionDef);
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
	 * Checks if the value of the <code>aggregationRecursiveUpdateAction</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>aggregationRecursiveUpdateAction</code> attribute is defined.
	 */
    public boolean isAggregationRecursiveUpdateActionDefined()
	{
	    return isDefined(aggregationRecursiveUpdateActionDef);
	}
 
    /**
     * Returns the value of the <code>aggregationTargetPaths</code> attribute.
     *
     * @return the value of the <code>aggregationTargetPaths</code> attribute.
     */
    public String getAggregationTargetPaths()
    {
        return get(aggregationTargetPathsDef);
    }
    
    /**
     * Returns the value of the <code>aggregationTargetPaths</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>aggregationTargetPaths</code> attribute.
     */
    public String getAggregationTargetPaths(String defaultValue)
    {
        return get(aggregationTargetPathsDef, defaultValue);
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
        return get(aggregationUpdateActionDef);
    }
    
    /**
     * Returns the value of the <code>aggregationUpdateAction</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>aggregationUpdateAction</code> attribute.
     */
    public String getAggregationUpdateAction(String defaultValue)
    {
        return get(aggregationUpdateActionDef, defaultValue);
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
	    Boolean value = get(categorizableDef);
        if(value != null)
        {
            return value.booleanValue();
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
		return get(categorizableDef, Boolean.valueOf(defaultValue)).booleanValue();
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
            set(categorizableDef, Boolean.valueOf(value));
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
        return get(editViewDef);
    }
    
    /**
     * Returns the value of the <code>editView</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>editView</code> attribute.
     */
    public String getEditView(String defaultValue)
    {
        return get(editViewDef, defaultValue);
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
        return get(imageDef);
    }
    
    /**
     * Returns the value of the <code>image</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>image</code> attribute.
     */
    public String getImage(String defaultValue)
    {
        return get(imageDef, defaultValue);
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
        return get(indexDescriptionDef);
    }
    
    /**
     * Returns the value of the <code>indexDescription</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>indexDescription</code> attribute.
     */
    public String getIndexDescription(String defaultValue)
    {
        return get(indexDescriptionDef, defaultValue);
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
        return get(indexTitleDef);
    }
    
    /**
     * Returns the value of the <code>indexTitle</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>indexTitle</code> attribute.
     */
    public String getIndexTitle(String defaultValue)
    {
        return get(indexTitleDef, defaultValue);
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
        return get(indexableFieldsDef);
    }
    
    /**
     * Returns the value of the <code>indexableFields</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>indexableFields</code> attribute.
     */
    public String getIndexableFields(String defaultValue)
    {
        return get(indexableFieldsDef, defaultValue);
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
     * Returns the value of the <code>pickerSupported</code> attribute.
     *
     * @return the value of the <code>pickerSupported</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getPickerSupported()
        throws IllegalStateException
    {
	    Boolean value = get(pickerSupportedDef);
        if(value != null)
        {
            return value.booleanValue();
        }
        else
        {
            throw new IllegalStateException("value of attribute pickerSupported is undefined"+
			    " for resource #"+getId());
        }
    }

    /**
     * Returns the value of the <code>pickerSupported</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>pickerSupported</code> attribute.
     */
    public boolean getPickerSupported(boolean defaultValue)
    {
		return get(pickerSupportedDef, Boolean.valueOf(defaultValue)).booleanValue();
	}

    /**
     * Sets the value of the <code>pickerSupported</code> attribute.
     *
     * @param value the value of the <code>pickerSupported</code> attribute.
     */
    public void setPickerSupported(boolean value)
    {
        try
        {
            set(pickerSupportedDef, Boolean.valueOf(value));
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
     * Removes the value of the <code>pickerSupported</code> attribute.
     */
    public void unsetPickerSupported()
    {
        try
        {
            unset(pickerSupportedDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>pickerSupported</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>pickerSupported</code> attribute is defined.
	 */
    public boolean isPickerSupportedDefined()
	{
	    return isDefined(pickerSupportedDef);
	}
 
    /**
     * Returns the value of the <code>relatedQuickAddView</code> attribute.
     *
     * @return the value of the <code>relatedQuickAddView</code> attribute.
     */
    public String getRelatedQuickAddView()
    {
        return get(relatedQuickAddViewDef);
    }
    
    /**
     * Returns the value of the <code>relatedQuickAddView</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>relatedQuickAddView</code> attribute.
     */
    public String getRelatedQuickAddView(String defaultValue)
    {
        return get(relatedQuickAddViewDef, defaultValue);
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
     * Returns the value of the <code>relatedQuickEditView</code> attribute.
     *
     * @return the value of the <code>relatedQuickEditView</code> attribute.
     */
    public String getRelatedQuickEditView()
    {
        return get(relatedQuickEditViewDef);
    }
    
    /**
     * Returns the value of the <code>relatedQuickEditView</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>relatedQuickEditView</code> attribute.
     */
    public String getRelatedQuickEditView(String defaultValue)
    {
        return get(relatedQuickEditViewDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>relatedQuickEditView</code> attribute.
     *
     * @param value the value of the <code>relatedQuickEditView</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setRelatedQuickEditView(String value)
    {
        try
        {
            if(value != null)
            {
                set(relatedQuickEditViewDef, value);
            }
            else
            {
                unset(relatedQuickEditViewDef);
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
	 * Checks if the value of the <code>relatedQuickEditView</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>relatedQuickEditView</code> attribute is defined.
	 */
    public boolean isRelatedQuickEditViewDefined()
	{
	    return isDefined(relatedQuickEditViewDef);
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
	    Boolean value = get(relatedSupportedDef);
        if(value != null)
        {
            return value.booleanValue();
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
		return get(relatedSupportedDef, Boolean.valueOf(defaultValue)).booleanValue();
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
            set(relatedSupportedDef, Boolean.valueOf(value));
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
        return get(viewDef);
    }
    
    /**
     * Returns the value of the <code>view</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>view</code> attribute.
     */
    public String getView(String defaultValue)
    {
        return get(viewDef, defaultValue);
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
