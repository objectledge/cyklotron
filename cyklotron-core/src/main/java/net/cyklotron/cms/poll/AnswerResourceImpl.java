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

import java.util.HashMap;
import java.util.Map;

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
 * An implementation of <code>cms.poll.answer</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class AnswerResourceImpl
    extends CmsNodeResourceImpl
    implements AnswerResource
{
    // class variables /////////////////////////////////////////////////////////

    /** Class variables initialization status. */
    private static boolean definitionsInitialized;
	
    /** The AttributeDefinition object for the <code>sequence</code> attribute. */
    private static AttributeDefinition sequenceDef;

    /** The AttributeDefinition object for the <code>votesCount</code> attribute. */
    private static AttributeDefinition votesCountDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>cms.poll.answer</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     */
    public AnswerResourceImpl()
    {
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>cms.poll.answer</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static AnswerResource getAnswerResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof AnswerResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not cms.poll.answer");
        }
        return (AnswerResource)res;
    }

    /**
     * Creates a new <code>cms.poll.answer</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @return a new AnswerResource instance.
     * @throws InvalidResourceNameException if the name argument contains illegal characters.
     */
    public static AnswerResource createAnswerResource(CoralSession session, String name,
        Resource parent)
        throws InvalidResourceNameException
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("cms.poll.answer");
            Map attrs = new HashMap();
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof AnswerResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (AnswerResource)res;
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
     * Returns the value of the <code>sequence</code> attribute.
     *
     * @return the value of the <code>sequence</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public int getSequence()
        throws IllegalStateException
    {
        if(isDefined(sequenceDef))
        {
            return ((Integer)get(sequenceDef)).intValue();
        }
        else
        {
            throw new IllegalStateException("value of attribute sequence is undefined"+
			    " for resource #"+getId());
        }
    }

    /**
     * Returns the value of the <code>sequence</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>sequence</code> attribute.
     */
    public int getSequence(int defaultValue)
    {
        if(isDefined(sequenceDef))
        {
            return ((Integer)get(sequenceDef)).intValue();
        }
        else
        {
            return defaultValue;
        }
    }

    /**
     * Sets the value of the <code>sequence</code> attribute.
     *
     * @param value the value of the <code>sequence</code> attribute.
     */
    public void setSequence(int value)
    {
        try
        {
            set(sequenceDef, new Integer(value));
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
     * Removes the value of the <code>sequence</code> attribute.
     */
    public void unsetSequence()
    {
        try
        {
            unset(sequenceDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>sequence</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>sequence</code> attribute is defined.
	 */
    public boolean isSequenceDefined()
	{
	    return isDefined(sequenceDef);
	}

    /**
     * Returns the value of the <code>votesCount</code> attribute.
     *
     * @return the value of the <code>votesCount</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public int getVotesCount()
        throws IllegalStateException
    {
        if(isDefined(votesCountDef))
        {
            return ((Integer)get(votesCountDef)).intValue();
        }
        else
        {
            throw new IllegalStateException("value of attribute votesCount is undefined"+
			    " for resource #"+getId());
        }
    }

    /**
     * Returns the value of the <code>votesCount</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>votesCount</code> attribute.
     */
    public int getVotesCount(int defaultValue)
    {
        if(isDefined(votesCountDef))
        {
            return ((Integer)get(votesCountDef)).intValue();
        }
        else
        {
            return defaultValue;
        }
    }

    /**
     * Sets the value of the <code>votesCount</code> attribute.
     *
     * @param value the value of the <code>votesCount</code> attribute.
     */
    public void setVotesCount(int value)
    {
        try
        {
            set(votesCountDef, new Integer(value));
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
     * Removes the value of the <code>votesCount</code> attribute.
     */
    public void unsetVotesCount()
    {
        try
        {
            unset(votesCountDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>votesCount</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>votesCount</code> attribute is defined.
	 */
    public boolean isVotesCountDefined()
	{
	    return isDefined(votesCountDef);
	}
  
    // @custom methods ///////////////////////////////////////////////////////
}
