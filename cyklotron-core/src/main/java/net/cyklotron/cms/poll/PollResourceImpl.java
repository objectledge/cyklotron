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
 
package net.cyklotron.cms.poll;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.objectledge.coral.BackendException;
import org.objectledge.coral.datatypes.NodeImpl;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.CoralSchema;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.ModificationNotPermitedException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;
import org.objectledge.database.Database;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.workflow.StateResource;
import org.jcontainer.dna.Logger;

/**
 * An implementation of <code>cms.poll.poll</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class PollResourceImpl
    extends NodeImpl
    implements PollResource
{
    // instance variables ////////////////////////////////////////////////////

    /** The AttributeDefinition object for the <code>end_date</code> attribute. */
    private AttributeDefinition end_dateDef;

    /** The AttributeDefinition object for the <code>moderator</code> attribute. */
    private AttributeDefinition moderatorDef;

    /** The AttributeDefinition object for the <code>start_date</code> attribute. */
    private AttributeDefinition start_dateDef;

    /** The AttributeDefinition object for the <code>state</code> attribute. */
    private AttributeDefinition stateDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>cms.poll.poll</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     * @param schema the CoralSchema.
     * @param database the Database.
     * @param logger the Logger.
     */
    public PollResourceImpl(CoralSchema schema, Database database, Logger logger)
    {
        super(schema, database, logger);
        try
        {
            ResourceClass rc = schema.getResourceClass("cms.poll.poll");
            end_dateDef = rc.getAttribute("end_date");
            moderatorDef = rc.getAttribute("moderator");
            start_dateDef = rc.getAttribute("start_date");
            stateDef = rc.getAttribute("state");
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>cms.poll.poll</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static PollResource getPollResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof PollResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not cms.poll.poll");
        }
        return (PollResource)res;
    }

    /**
     * Creates a new <code>cms.poll.poll</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @return a new PollResource instance.
     */
    public static PollResource createPollResource(CoralSession session, String name, Resource
        parent)
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("cms.poll.poll");
            Map attrs = new HashMap();
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof PollResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (PollResource)res;
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
     * Returns the value of the <code>end_date</code> attribute.
     *
     * @return the value of the <code>end_date</code> attribute.
     */
    public Date getEnd_date()
    {
        return (Date)get(end_dateDef);
    }
    
    /**
     * Returns the value of the <code>end_date</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>end_date</code> attribute.
     */
    public Date getEnd_date(Date defaultValue)
    {
        if(isDefined(end_dateDef))
        {
            return (Date)get(end_dateDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>end_date</code> attribute.
     *
     * @param value the value of the <code>end_date</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setEnd_date(Date value)
    {
        try
        {
            if(value != null)
            {
                set(end_dateDef, value);
            }
            else
            {
                unset(end_dateDef);
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
	 * Checks if the value of the <code>end_date</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>end_date</code> attribute is defined.
	 */
    public boolean isEnd_dateDefined()
	{
	    return isDefined(end_dateDef);
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
     * Returns the value of the <code>start_date</code> attribute.
     *
     * @return the value of the <code>start_date</code> attribute.
     */
    public Date getStart_date()
    {
        return (Date)get(start_dateDef);
    }
    
    /**
     * Returns the value of the <code>start_date</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>start_date</code> attribute.
     */
    public Date getStart_date(Date defaultValue)
    {
        if(isDefined(start_dateDef))
        {
            return (Date)get(start_dateDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>start_date</code> attribute.
     *
     * @param value the value of the <code>start_date</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setStart_date(Date value)
    {
        try
        {
            if(value != null)
            {
                set(start_dateDef, value);
            }
            else
            {
                unset(start_dateDef);
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
	 * Checks if the value of the <code>start_date</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>start_date</code> attribute is defined.
	 */
    public boolean isStart_dateDefined()
	{
	    return isDefined(start_dateDef);
	}
 
    /**
     * Returns the value of the <code>state</code> attribute.
     *
     * @return the value of the <code>state</code> attribute.
     */
    public StateResource getState()
    {
        return (StateResource)get(stateDef);
    }
    
    /**
     * Returns the value of the <code>state</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>state</code> attribute.
     */
    public StateResource getState(StateResource defaultValue)
    {
        if(isDefined(stateDef))
        {
            return (StateResource)get(stateDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>state</code> attribute.
     *
     * @param value the value of the <code>state</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setState(StateResource value)
    {
        try
        {
            if(value != null)
            {
                set(stateDef, value);
            }
            else
            {
                unset(stateDef);
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
	 * Checks if the value of the <code>state</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>state</code> attribute is defined.
	 */
    public boolean isStateDefined()
	{
	    return isDefined(stateDef);
	}
  
    // @custom methods ///////////////////////////////////////////////////////
    // @extends coral.Node
    // @import net.cyklotron.cms.CmsData
    // @import net.cyklotron.cms.poll.QuestionResource
    
    public int getMaxVotes()
    	throws Exception
	{
		int max = 0;
	    Resource[] questionResources = rs.getStore().getResource(this);
	    for(int i = 0; i < questionResources.length; i++)
	    {
	        QuestionResource questionResource = (QuestionResource)questionResources[i];
	        if(questionResource.getVotesCount() > max)
	        {
	        	max = questionResource.getVotesCount();
	        }
	    }
	    return max;
	}
    
    /**
     * Checks if this resource can be viewed at the given time.
     */
    public boolean isValid(Date time)
    {
        if(time.before(getStartDate()))
        {
            return false;
        }
        return time.before(getEndDate());
    }

    public boolean canView(Subject subject)
    {
        return true;
    }

    /**
     * Checks if the specified subject can modify this resource.
     */
    public boolean canModify(Subject subject)
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Checks if the specified subject can remove this resource.
     */
    public boolean canRemove(Subject subject)
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Checks if the specified subject can add children to this resource.
     */
    public boolean canAddChild(Subject subject)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Checks if the specified subject can view this resource at the given time.
     */
    public boolean canView(Subject subject, Date time)
    {
        if(!canView(subject))
        {
            return false;
        }
        return isValid(time);
    }

    /**
     * Checks if the specified subject can view this resource
     */
    public boolean canView(CmsData data, Subject subject)
    {
        if(data.getBrowseMode().equals(CmsData.BROWSE_MODE_ADMINISTER))
        {
            return canView(subject);
        }
        else
        {
            return canView(subject, data.getDate());
        }
    }

    public String getIndexAbbreviation()
    {
        return getDescription();
    }
    
    PollService pollService = null;
    
    public String getIndexContent()
    {
        if(pollService == null)
        {
            pollService = (PollService)Labeo.getBroker().getService(PollService.SERVICE_NAME);
        }
        return pollService.getPollContent(this);
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
