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
 
package net.cyklotron.cms.files;

import org.objectledge.coral.BackendException;
import org.objectledge.coral.datatypes.ResourceList;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.ModificationNotPermitedException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;

/**
 * An implementation of <code>cms.files.files_map</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class FilesMapResourceImpl
    extends ItemResourceImpl
    implements FilesMapResource
{
    // class variables /////////////////////////////////////////////////////////

    /** Class variables initialization status. */
	@SuppressWarnings("unused")
    private static boolean definitionsInitialized;
	
    /** The AttributeDefinition object for the <code>administrator</code> attribute. */
	private static AttributeDefinition<Role> administratorDef;

    /** The AttributeDefinition object for the <code>expandedDirectory</code> attribute. */
	private static AttributeDefinition<Resource> expandedDirectoryDef;

    /** The AttributeDefinition object for the <code>frontCategories</code> attribute. */
	private static AttributeDefinition<ResourceList> frontCategoriesDef;

    /** The AttributeDefinition object for the <code>visitor</code> attribute. */
	private static AttributeDefinition<Role> visitorDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>cms.files.files_map</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     */
    public FilesMapResourceImpl()
    {
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>cms.files.files_map</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static FilesMapResource getFilesMapResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof FilesMapResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not cms.files.files_map");
        }
        return (FilesMapResource)res;
    }

    /**
     * Creates a new <code>cms.files.files_map</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @return a new FilesMapResource instance.
     * @throws InvalidResourceNameException if the name argument contains illegal characters.
     */
    public static FilesMapResource createFilesMapResource(CoralSession session, String name,
        Resource parent)
        throws InvalidResourceNameException
    {
        try
        {
            ResourceClass<FilesMapResource> rc = session.getSchema().getResourceClass("cms.files.files_map", FilesMapResource.class);
		    Resource res = session.getStore().createResource(name, parent, rc,
                java.util.Collections.<AttributeDefinition<?>, Object> emptyMap());			
            if(!(res instanceof FilesMapResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (FilesMapResource)res;
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
     * Returns the value of the <code>administrator</code> attribute.
     *
     * @return the value of the <code>administrator</code> attribute.
     */
    public Role getAdministrator()
    {
        return get(administratorDef);
    }
    
    /**
     * Returns the value of the <code>administrator</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>administrator</code> attribute.
     */
    public Role getAdministrator(Role defaultValue)
    {
        return get(administratorDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>administrator</code> attribute.
     *
     * @param value the value of the <code>administrator</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAdministrator(Role value)
    {
        try
        {
            if(value != null)
            {
                set(administratorDef, value);
            }
            else
            {
                unset(administratorDef);
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
	 * Checks if the value of the <code>administrator</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>administrator</code> attribute is defined.
	 */
    public boolean isAdministratorDefined()
	{
	    return isDefined(administratorDef);
	}
 
    /**
     * Returns the value of the <code>expandedDirectory</code> attribute.
     *
     * @return the value of the <code>expandedDirectory</code> attribute.
     */
    public Resource getExpandedDirectory()
    {
        return get(expandedDirectoryDef);
    }
    
    /**
     * Returns the value of the <code>expandedDirectory</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>expandedDirectory</code> attribute.
     */
    public Resource getExpandedDirectory(Resource defaultValue)
    {
        return get(expandedDirectoryDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>expandedDirectory</code> attribute.
     *
     * @param value the value of the <code>expandedDirectory</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setExpandedDirectory(Resource value)
    {
        try
        {
            if(value != null)
            {
                set(expandedDirectoryDef, value);
            }
            else
            {
                unset(expandedDirectoryDef);
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
	 * Checks if the value of the <code>expandedDirectory</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>expandedDirectory</code> attribute is defined.
	 */
    public boolean isExpandedDirectoryDefined()
	{
	    return isDefined(expandedDirectoryDef);
	}
 
    /**
     * Returns the value of the <code>frontCategories</code> attribute.
     *
     * @return the value of the <code>frontCategories</code> attribute.
     */
    public ResourceList getFrontCategories()
    {
        return get(frontCategoriesDef);
    }
    
    /**
     * Returns the value of the <code>frontCategories</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>frontCategories</code> attribute.
     */
    public ResourceList getFrontCategories(ResourceList defaultValue)
    {
        return get(frontCategoriesDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>frontCategories</code> attribute.
     *
     * @param value the value of the <code>frontCategories</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setFrontCategories(ResourceList value)
    {
        try
        {
            if(value != null)
            {
                set(frontCategoriesDef, value);
            }
            else
            {
                unset(frontCategoriesDef);
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
	 * Checks if the value of the <code>frontCategories</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>frontCategories</code> attribute is defined.
	 */
    public boolean isFrontCategoriesDefined()
	{
	    return isDefined(frontCategoriesDef);
	}
 
    /**
     * Returns the value of the <code>visitor</code> attribute.
     *
     * @return the value of the <code>visitor</code> attribute.
     */
    public Role getVisitor()
    {
        return get(visitorDef);
    }
    
    /**
     * Returns the value of the <code>visitor</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>visitor</code> attribute.
     */
    public Role getVisitor(Role defaultValue)
    {
        return get(visitorDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>visitor</code> attribute.
     *
     * @param value the value of the <code>visitor</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setVisitor(Role value)
    {
        try
        {
            if(value != null)
            {
                set(visitorDef, value);
            }
            else
            {
                unset(visitorDef);
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
	 * Checks if the value of the <code>visitor</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>visitor</code> attribute is defined.
	 */
    public boolean isVisitorDefined()
	{
	    return isDefined(visitorDef);
	}
  
    // @custom methods ///////////////////////////////////////////////////////
}
