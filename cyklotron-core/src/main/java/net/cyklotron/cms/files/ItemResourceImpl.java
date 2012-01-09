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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.objectledge.coral.BackendException;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.ModificationNotPermitedException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;

import net.cyklotron.cms.CmsConstants;
import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsNodeResourceImpl;

/**
 * An implementation of <code>cms.files.item</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class ItemResourceImpl
    extends CmsNodeResourceImpl
    implements ItemResource
{
    // class variables /////////////////////////////////////////////////////////

    /** Class variables initialization status. */
    private static boolean definitionsInitialized;
	
    /** The AttributeDefinition object for the <code>hidden</code> attribute. */
    private static AttributeDefinition hiddenDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>cms.files.item</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     */
    public ItemResourceImpl()
    {
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>cms.files.item</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static ItemResource getItemResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof ItemResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not cms.files.item");
        }
        return (ItemResource)res;
    }

    /**
     * Creates a new <code>cms.files.item</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @return a new ItemResource instance.
     * @throws InvalidResourceNameException if the name argument contains illegal characters.
     */
    public static ItemResource createItemResource(CoralSession session, String name, Resource
        parent)
        throws InvalidResourceNameException
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("cms.files.item");
            Map attrs = new HashMap();
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof ItemResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (ItemResource)res;
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
     * Returns the value of the <code>hidden</code> attribute.
     *
     * @return the value of the <code>hidden</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getHidden()
        throws IllegalStateException
    {
	    Boolean value = (Boolean)getInternal(hiddenDef, null);
        if(value != null)
        {
            return value.booleanValue();
        }
        else
        {
            throw new IllegalStateException("value of attribute hidden is undefined"+
			    " for resource #"+getId());
        }
    }

    /**
     * Returns the value of the <code>hidden</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>hidden</code> attribute.
     */
    public boolean getHidden(boolean defaultValue)
    {
		return ((Boolean)getInternal(hiddenDef, new Boolean(defaultValue))).booleanValue();
	}

    /**
     * Sets the value of the <code>hidden</code> attribute.
     *
     * @param value the value of the <code>hidden</code> attribute.
     */
    public void setHidden(boolean value)
    {
        try
        {
            set(hiddenDef, new Boolean(value));
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
     * Removes the value of the <code>hidden</code> attribute.
     */
    public void unsetHidden()
    {
        try
        {
            unset(hiddenDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>hidden</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>hidden</code> attribute is defined.
	 */
    public boolean isHiddenDefined()
	{
	    return isDefined(hiddenDef);
	}
  
    // @custom methods ///////////////////////////////////////////////////////
    // @extends node
    // @import java.util.Date
    // @import net.cyklotron.cms.CmsData
    // @import org.objectledge.coral.security.Permission
    // @import org.objectledge.coral.security.Subject
    // @import org.objectledge.coral.session.CoralSession
    // @import net.cyklotron.cms.CmsConstants

    
    /**
     * Returns the path relative to site's files root node.
     *
     * @return path relative to site's files node.
     */
    public String getSitePath()
    {
        Resource root = this;
        while(root != null)
        {
            root = root.getParent();
            if(root instanceof net.cyklotron.cms.files.FilesMapResource)
            {
                break;
            }
        }
        String fullPath = getPath();
        if(root == null)
        {
            return fullPath;
        }
        String relativePath = fullPath.substring(root.getPath().length());
        return relativePath;
    }
    
    /**
     * Checks if this resource can be viewed at the given time.
     */
    public boolean isValid(Date time)
    {
        return true;
    }

    /** the navigation node view permission */
    private Permission viewPermission;
    
    /**
     * Checks if a given subject can view this resource.
     */
    public boolean canView(CoralSession coralSession, Subject subject)
    {
        if(viewPermission == null)
        {
            viewPermission = coralSession.getSecurity().getUniquePermission("cms.files.read");
        }
        // check view permission
        return subject.hasPermission(this, viewPermission);
    }

    /** the modify permission */
    private Permission modifyPermission;

    /**
     * Checks if the specified subject can modify this resource.
     */
    public boolean canModify(CoralSession coralSession, Subject subject)
    {
        if(modifyPermission == null)
        {
            modifyPermission = coralSession.getSecurity().getUniquePermission("cms.files.modify");
        }
        // check modify permission
        return subject.hasPermission(this, modifyPermission);
    }

    /** the view permission */
    private Permission deletePermission;

    /**
     * Checks if the specified subject can remove this resource.
     */
    public boolean canRemove(CoralSession coralSession, Subject subject)
    {
        if(deletePermission == null)
        {
            deletePermission = coralSession.getSecurity().getUniquePermission("cms.files.delete");
        }
        // check delete permission
        return subject.hasPermission(this, deletePermission);
    }
    
    /** the view permission */
    private Permission writePermission;

    /**
     * Checks if the specified subject can add children to this resource.
     */
    public boolean canAddChild(CoralSession coralSession, Subject subject)
    {
        if(writePermission == null)
        {
            writePermission = coralSession.getSecurity().getUniquePermission("cms.files.write");
        }
        // check write permission
        return subject.hasPermission(this, writePermission);
    }

    /**
     * Checks if the specified subject can view this resource at the given time.
     */
    public boolean canView(CoralSession coralSession, Subject subject, Date time)
    {
        return canView(coralSession, subject);
    }

    /**
     * Checks if the specified subject can view this resource
     */
    public boolean canView(CoralSession coralSession, CmsData data, Subject subject)
    {
        if(data.getBrowseMode().equals(CmsConstants.BROWSE_MODE_ADMINISTER))
        {
            return canView(coralSession, subject);
        }
        else
        {
            return canView(coralSession, subject, data.getDate());
        }
    }

    public String getIndexAbbreviation()
    {
        
        return getDescription();
    }
    
    public String getIndexContent()
    {
        return null;
    }
    
    public String getIndexTitle()
    {
        return getName();
    }
    
    public Object getFieldValue(String fieldName)
    {
        return null;
    }

	/**
	 * Returns the store flag of the field.
	 *
	 * @return the store flag.
	 */
	public boolean isStored(String fieldName)
	{
		return false;
	}
	
	/**
	 * Returns the indexed flag of the field.
	 *
	 * @return the indexed flag.
	 */
	public boolean isIndexed(String fieldName)
	{
		return false;
	}
		
	/**
	 * Returns the tokenized flag of the field.
	 *
	 * @return the tokenized flag.
	 */
	public boolean isTokenized(String fieldName)
	{
		return false;
	}
}
