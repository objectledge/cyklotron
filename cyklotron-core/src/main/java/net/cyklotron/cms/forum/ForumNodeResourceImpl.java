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
 
package net.cyklotron.cms.forum;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.objectledge.context.Context;
import org.objectledge.coral.BackendException;
import org.objectledge.coral.datatypes.WeakResourceList;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.CoralSchema;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.ModificationNotPermitedException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;
import org.objectledge.database.Database;

import net.cyklotron.cms.CmsConstants;
import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsNodeResourceImpl;
import org.jcontainer.dna.Logger;

/**
 * An implementation of <code>cms.forum.node</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class ForumNodeResourceImpl
    extends CmsNodeResourceImpl
    implements ForumNodeResource
{
    // instance variables ////////////////////////////////////////////////////

    /** The AttributeDefinition object for the <code>administrator</code> attribute. */
    private AttributeDefinition administratorDef;

    /** The AttributeDefinition object for the <code>lastlyAdded</code> attribute. */
    private AttributeDefinition lastlyAddedDef;

    /** The AttributeDefinition object for the <code>lastlyAddedSize</code> attribute. */
    private AttributeDefinition lastlyAddedSizeDef;

    /** The AttributeDefinition object for the <code>moderator</code> attribute. */
    private AttributeDefinition moderatorDef;

    /** The AttributeDefinition object for the <code>participant</code> attribute. */
    private AttributeDefinition participantDef;

    /** The AttributeDefinition object for the <code>visitor</code> attribute. */
    private AttributeDefinition visitorDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>cms.forum.node</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     * @param schema the CoralSchema.
     * @param database the Database.
     * @param logger the Logger.
     */
    public ForumNodeResourceImpl(CoralSchema schema, Database database, Logger logger)
    {
        super(schema, database, logger);
        try
        {
            ResourceClass rc = schema.getResourceClass("cms.forum.node");
            administratorDef = rc.getAttribute("administrator");
            lastlyAddedDef = rc.getAttribute("lastlyAdded");
            lastlyAddedSizeDef = rc.getAttribute("lastlyAddedSize");
            moderatorDef = rc.getAttribute("moderator");
            participantDef = rc.getAttribute("participant");
            visitorDef = rc.getAttribute("visitor");
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>cms.forum.node</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static ForumNodeResource getForumNodeResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof ForumNodeResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not cms.forum.node");
        }
        return (ForumNodeResource)res;
    }

    /**
     * Creates a new <code>cms.forum.node</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @return a new ForumNodeResource instance.
     * @throws InvalidResourceNameException if the name argument contains illegal characters.
     */
    public static ForumNodeResource createForumNodeResource(CoralSession session, String name,
        Resource parent)
        throws InvalidResourceNameException
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("cms.forum.node");
            Map attrs = new HashMap();
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof ForumNodeResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (ForumNodeResource)res;
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
        return (Role)get(administratorDef);
    }
    
    /**
     * Returns the value of the <code>administrator</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>administrator</code> attribute.
     */
    public Role getAdministrator(Role defaultValue)
    {
        if(isDefined(administratorDef))
        {
            return (Role)get(administratorDef);
        }
        else
        {
            return defaultValue;
        }
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
     * Returns the value of the <code>lastlyAdded</code> attribute.
     *
     * @return the value of the <code>lastlyAdded</code> attribute.
     */
    public WeakResourceList getLastlyAdded()
    {
        return (WeakResourceList)get(lastlyAddedDef);
    }
    
    /**
     * Returns the value of the <code>lastlyAdded</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>lastlyAdded</code> attribute.
     */
    public WeakResourceList getLastlyAdded(WeakResourceList defaultValue)
    {
        if(isDefined(lastlyAddedDef))
        {
            return (WeakResourceList)get(lastlyAddedDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>lastlyAdded</code> attribute.
     *
     * @param value the value of the <code>lastlyAdded</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setLastlyAdded(WeakResourceList value)
    {
        try
        {
            if(value != null)
            {
                set(lastlyAddedDef, value);
            }
            else
            {
                unset(lastlyAddedDef);
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
	 * Checks if the value of the <code>lastlyAdded</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>lastlyAdded</code> attribute is defined.
	 */
    public boolean isLastlyAddedDefined()
	{
	    return isDefined(lastlyAddedDef);
	}

    /**
     * Returns the value of the <code>lastlyAddedSize</code> attribute.
     *
     * @return the value of the <code>lastlyAddedSize</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public int getLastlyAddedSize()
        throws IllegalStateException
    {
        if(isDefined(lastlyAddedSizeDef))
        {
            return ((Integer)get(lastlyAddedSizeDef)).intValue();
        }
        else
        {
            throw new IllegalStateException("value of attribute lastlyAddedSize is undefined"+
			    " for resource #"+getId());
        }
    }

    /**
     * Returns the value of the <code>lastlyAddedSize</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>lastlyAddedSize</code> attribute.
     */
    public int getLastlyAddedSize(int defaultValue)
    {
        if(isDefined(lastlyAddedSizeDef))
        {
            return ((Integer)get(lastlyAddedSizeDef)).intValue();
        }
        else
        {
            return defaultValue;
        }
    }

    /**
     * Sets the value of the <code>lastlyAddedSize</code> attribute.
     *
     * @param value the value of the <code>lastlyAddedSize</code> attribute.
     */
    public void setLastlyAddedSize(int value)
    {
        try
        {
            set(lastlyAddedSizeDef, new Integer(value));
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
     * Removes the value of the <code>lastlyAddedSize</code> attribute.
     */
    public void unsetLastlyAddedSize()
    {
        try
        {
            unset(lastlyAddedSizeDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>lastlyAddedSize</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>lastlyAddedSize</code> attribute is defined.
	 */
    public boolean isLastlyAddedSizeDefined()
	{
	    return isDefined(lastlyAddedSizeDef);
	}
 
    /**
     * Returns the value of the <code>moderator</code> attribute.
     *
     * @return the value of the <code>moderator</code> attribute.
     */
    public Role getModerator()
    {
        return (Role)get(moderatorDef);
    }
    
    /**
     * Returns the value of the <code>moderator</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>moderator</code> attribute.
     */
    public Role getModerator(Role defaultValue)
    {
        if(isDefined(moderatorDef))
        {
            return (Role)get(moderatorDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>moderator</code> attribute.
     *
     * @param value the value of the <code>moderator</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setModerator(Role value)
    {
        try
        {
            if(value != null)
            {
                set(moderatorDef, value);
            }
            else
            {
                unset(moderatorDef);
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
	 * Checks if the value of the <code>moderator</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>moderator</code> attribute is defined.
	 */
    public boolean isModeratorDefined()
	{
	    return isDefined(moderatorDef);
	}
 
    /**
     * Returns the value of the <code>participant</code> attribute.
     *
     * @return the value of the <code>participant</code> attribute.
     */
    public Role getParticipant()
    {
        return (Role)get(participantDef);
    }
    
    /**
     * Returns the value of the <code>participant</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>participant</code> attribute.
     */
    public Role getParticipant(Role defaultValue)
    {
        if(isDefined(participantDef))
        {
            return (Role)get(participantDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>participant</code> attribute.
     *
     * @param value the value of the <code>participant</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setParticipant(Role value)
    {
        try
        {
            if(value != null)
            {
                set(participantDef, value);
            }
            else
            {
                unset(participantDef);
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
	 * Checks if the value of the <code>participant</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>participant</code> attribute is defined.
	 */
    public boolean isParticipantDefined()
	{
	    return isDefined(participantDef);
	}
 
    /**
     * Returns the value of the <code>visitor</code> attribute.
     *
     * @return the value of the <code>visitor</code> attribute.
     */
    public Role getVisitor()
    {
        return (Role)get(visitorDef);
    }
    
    /**
     * Returns the value of the <code>visitor</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>visitor</code> attribute.
     */
    public Role getVisitor(Role defaultValue)
    {
        if(isDefined(visitorDef))
        {
            return (Role)get(visitorDef);
        }
        else
        {
            return defaultValue;
        }
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
    // @extends node
    // @import net.cyklotron.cms.CmsData
    // @import java.util.Date
    // @import org.objectledge.context.Context
    // @import org.objectledge.coral.security.Permission
    // @import org.objectledge.coral.security.Subject
    // @import org.objectledge.coral.session.CoralSession
    // @import net.cyklotron.cms.CmsConstants
    
    /**
     * Checks if this resource can be viewed at the given time.
     */
    public boolean isValid(Date time)
    {
        return true;
    }

    /**
     * Checks if a given subject can view this resource.
     */
    public boolean canView(CoralSession coralSession, Subject subject)
    {
        return true;
    }

    /**
     * Checks if the specified subject can view this resource at the given time.
     */
    public boolean canView(CoralSession coralSession, Subject subject, Date time)
    {
        return true;
    }

    /** the message modify permission */
    private Permission modifyPermission;

    /**
     * Checks if the specified subject can modify this resource.
     */
    public boolean canModify(CoralSession coralSession, Subject subject)
    {
        if(modifyPermission == null)
        {
            modifyPermission = coralSession.getSecurity().getUniquePermission("cms.forum.modify");
        }
        return subject.hasPermission(this, modifyPermission);
    }
    
    /**
     * Checks if the specified subject can remove this resource.
     */
    public boolean canRemove(Context context, Subject subject)
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Checks if the specified subject can add children to this resource.
     */
    public boolean canAddChild(Context context, Subject subject)
    {
        throw new UnsupportedOperationException();
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
