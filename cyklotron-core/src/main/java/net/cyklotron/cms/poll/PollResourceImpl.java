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

import org.objectledge.context.Context;
import org.objectledge.coral.BackendException;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.CoralSchema;
import org.objectledge.coral.schema.ResourceClass;
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
import net.cyklotron.cms.workflow.StateResource;
import org.jcontainer.dna.Logger;

/**
 * An implementation of <code>cms.poll.poll</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class PollResourceImpl
    extends CmsNodeResourceImpl
    implements PollResource
{
    // instance variables ////////////////////////////////////////////////////

    /** The AttributeDefinition object for the <code>endDate</code> attribute. */
    private AttributeDefinition endDateDef;

    /** The AttributeDefinition object for the <code>moderator</code> attribute. */
    private AttributeDefinition moderatorDef;

    /** The AttributeDefinition object for the <code>startDate</code> attribute. */
    private AttributeDefinition startDateDef;

    /** The AttributeDefinition object for the <code>state</code> attribute. */
    private AttributeDefinition stateDef;

	// custom injected fields /////////////////////////////////////////////////
	
    /** The PollService. */
    protected PollService pollService;

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
     * @param pollService the PollService.
     */
    public PollResourceImpl(CoralSchema schema, Database database, Logger logger, PollService
        pollService)
    {
        super(schema, database, logger);
        try
        {
            ResourceClass rc = schema.getResourceClass("cms.poll.poll");
            endDateDef = rc.getAttribute("endDate");
            moderatorDef = rc.getAttribute("moderator");
            startDateDef = rc.getAttribute("startDate");
            stateDef = rc.getAttribute("state");
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
        this.pollService = pollService;
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
     * @throws InvalidResourceNameException if the name argument contains illegal characters.
     */
    public static PollResource createPollResource(CoralSession session, String name, Resource
        parent)
        throws InvalidResourceNameException
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
     * Returns the value of the <code>endDate</code> attribute.
     *
     * @return the value of the <code>endDate</code> attribute.
     */
    public Date getEndDate()
    {
        return (Date)get(endDateDef);
    }
    
    /**
     * Returns the value of the <code>endDate</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>endDate</code> attribute.
     */
    public Date getEndDate(Date defaultValue)
    {
        if(isDefined(endDateDef))
        {
            return (Date)get(endDateDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>endDate</code> attribute.
     *
     * @param value the value of the <code>endDate</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setEndDate(Date value)
    {
        try
        {
            if(value != null)
            {
                set(endDateDef, value);
            }
            else
            {
                unset(endDateDef);
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
	 * Checks if the value of the <code>endDate</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>endDate</code> attribute is defined.
	 */
    public boolean isEndDateDefined()
	{
	    return isDefined(endDateDef);
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
     * Returns the value of the <code>startDate</code> attribute.
     *
     * @return the value of the <code>startDate</code> attribute.
     */
    public Date getStartDate()
    {
        return (Date)get(startDateDef);
    }
    
    /**
     * Returns the value of the <code>startDate</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>startDate</code> attribute.
     */
    public Date getStartDate(Date defaultValue)
    {
        if(isDefined(startDateDef))
        {
            return (Date)get(startDateDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>startDate</code> attribute.
     *
     * @param value the value of the <code>startDate</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setStartDate(Date value)
    {
        try
        {
            if(value != null)
            {
                set(startDateDef, value);
            }
            else
            {
                unset(startDateDef);
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
	 * Checks if the value of the <code>startDate</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>startDate</code> attribute is defined.
	 */
    public boolean isStartDateDefined()
	{
	    return isDefined(startDateDef);
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
    // @extends node
    // @import net.cyklotron.cms.CmsData
    // @import net.cyklotron.cms.poll.QuestionResource
    // @import net.cyklotron.cms.poll.PollService
    // @import org.objectledge.coral.session.CoralSession
    // @import org.objectledge.context.Context
    // @import org.objectledge.coral.security.Subject
    // @import net.cyklotron.cms.CmsConstants
    // @field PollService pollService
    
    public int getMaxVotes(CoralSession coralSession)
    	throws Exception
	{
		int max = 0;
	    Resource[] questionResources = coralSession.getStore().getResource(this);
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

    public boolean canView(CoralSession coralSession, Subject subject)
    {
        return true;
    }

    /**
     * Checks if the specified subject can modify this resource.
     */
    public boolean canModify(CoralSession coralSession, Subject subject)
    {
        throw new UnsupportedOperationException();
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
     * Checks if the specified subject can view this resource at the given time.
     */
    public boolean canView(CoralSession coralSession, Subject subject, Date time)
    {
        if(!canView(coralSession, subject))
        {
            return false;
        }
        return isValid(time);
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
