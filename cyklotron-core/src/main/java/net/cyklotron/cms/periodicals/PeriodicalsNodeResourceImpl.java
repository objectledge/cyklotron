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
 
package net.cyklotron.cms.periodicals;

import java.util.HashMap;
import java.util.Map;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.BackendException;
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

import net.cyklotron.cms.ProtectedResourceImpl;

/**
 * An implementation of <code>cms.periodicals.node</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class PeriodicalsNodeResourceImpl
    extends ProtectedResourceImpl
    implements PeriodicalsNodeResource
{
    // instance variables ////////////////////////////////////////////////////

    /** The AttributeDefinition object for the <code>administrator</code> attribute. */
    private AttributeDefinition administratorDef;

    /** The AttributeDefinition object for the <code>description</code> attribute. */
    private AttributeDefinition descriptionDef;

    /** The AttributeDefinition object for the <code>visitor</code> attribute. */
    private AttributeDefinition visitorDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>cms.periodicals.node</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     * @param schema the CoralSchema.
     * @param database the Database.
     * @param logger the Logger.
     */
    public PeriodicalsNodeResourceImpl(CoralSchema schema, Database database, Logger logger)
    {
        super(schema, database, logger);
        try
        {
            ResourceClass rc = schema.getResourceClass("cms.periodicals.node");
            administratorDef = rc.getAttribute("administrator");
            descriptionDef = rc.getAttribute("description");
            visitorDef = rc.getAttribute("visitor");
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>cms.periodicals.node</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static PeriodicalsNodeResource getPeriodicalsNodeResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof PeriodicalsNodeResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not cms.periodicals.node");
        }
        return (PeriodicalsNodeResource)res;
    }

    /**
     * Creates a new <code>cms.periodicals.node</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @return a new PeriodicalsNodeResource instance.
     */
    public static PeriodicalsNodeResource createPeriodicalsNodeResource(CoralSession session,
        String name, Resource parent)
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("cms.periodicals.node");
            Map attrs = new HashMap();
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof PeriodicalsNodeResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (PeriodicalsNodeResource)res;
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
     * Returns the value of the <code>description</code> attribute.
     *
     * @return the value of the <code>description</code> attribute.
     */
    public String getDescription()
    {
        return (String)get(descriptionDef);
    }
    
    /**
     * Returns the value of the <code>description</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>description</code> attribute.
     */
    public String getDescription(String defaultValue)
    {
        if(isDefined(descriptionDef))
        {
            return (String)get(descriptionDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>description</code> attribute.
     *
     * @param value the value of the <code>description</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setDescription(String value)
    {
        try
        {
            if(value != null)
            {
                set(descriptionDef, value);
            }
            else
            {
                unset(descriptionDef);
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
	 * Checks if the value of the <code>description</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>description</code> attribute is defined.
	 */
    public boolean isDescriptionDefined()
	{
	    return isDefined(descriptionDef);
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
    
    //@extends protected
}
