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
 
package net.cyklotron.cms.search;

import java.util.HashMap;
import java.util.Map;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.BackendException;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.CoralSchema;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.ModificationNotPermitedException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;
import org.objectledge.database.Database;

import net.cyklotron.cms.CmsNodeResourceImpl;

/**
 * An implementation of <code>search.index</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class IndexResourceImpl
    extends CmsNodeResourceImpl
    implements IndexResource
{
    // instance variables ////////////////////////////////////////////////////

    /** The AttributeDefinition object for the <code>filesLocation</code> attribute. */
    private AttributeDefinition filesLocationDef;

    /** The AttributeDefinition object for the <code>optimise</code> attribute. */
    private AttributeDefinition optimiseDef;

    /** The AttributeDefinition object for the <code>public</code> attribute. */
    private AttributeDefinition publicDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>search.index</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     * @param schema the CoralSchema.
     * @param database the Database.
     * @param logger the Logger.
     */
    public IndexResourceImpl(CoralSchema schema, Database database, Logger logger)
    {
        super(schema, database, logger);
        try
        {
            ResourceClass rc = schema.getResourceClass("search.index");
            filesLocationDef = rc.getAttribute("filesLocation");
            optimiseDef = rc.getAttribute("optimise");
            publicDef = rc.getAttribute("public");
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>search.index</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static IndexResource getIndexResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof IndexResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not search.index");
        }
        return (IndexResource)res;
    }

    /**
     * Creates a new <code>search.index</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @param filesLocation the filesLocation attribute
     * @return a new IndexResource instance.
     * @throws ValueRequiredException if one of the required attribues is undefined.
     */
    public static IndexResource createIndexResource(CoralSession session, String name, Resource
        parent, String filesLocation)
        throws ValueRequiredException
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("search.index");
            Map attrs = new HashMap();
            attrs.put(rc.getAttribute("filesLocation"), filesLocation);
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof IndexResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (IndexResource)res;
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>filesLocation</code> attribute.
     *
     * @return the value of the <code>filesLocation</code> attribute.
     */
    public String getFilesLocation()
    {
        return (String)get(filesLocationDef);
    }
 
    /**
     * Sets the value of the <code>filesLocation</code> attribute.
     *
     * @param value the value of the <code>filesLocation</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setFilesLocation(String value)
        throws ValueRequiredException
    {
        try
        {
            if(value != null)
            {
                set(filesLocationDef, value);
            }
            else
            {
                throw new ValueRequiredException("attribute filesLocation "+
                                                 "is declared as REQUIRED");
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
    /**
     * Returns the value of the <code>optimise</code> attribute.
     *
     * @return the value of the <code>optimise</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getOptimise()
        throws IllegalStateException
    {
        if(isDefined(optimiseDef))
        {
            return ((Boolean)get(optimiseDef)).booleanValue();
        }
        else
        {
            throw new IllegalStateException("value of attribute optimise is undefined"+
			    " for resource #"+getId());
        }
    }

    /**
     * Returns the value of the <code>optimise</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>optimise</code> attribute.
     */
    public boolean getOptimise(boolean defaultValue)
    {
        if(isDefined(optimiseDef))
        {
            return ((Boolean)get(optimiseDef)).booleanValue();
        }
        else
        {
            return defaultValue;
        }
    }

    /**
     * Sets the value of the <code>optimise</code> attribute.
     *
     * @param value the value of the <code>optimise</code> attribute.
     */
    public void setOptimise(boolean value)
    {
        try
        {
            set(optimiseDef, new Boolean(value));
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
     * Removes the value of the <code>optimise</code> attribute.
     */
    public void unsetOptimise()
    {
        try
        {
            unset(optimiseDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>optimise</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>optimise</code> attribute is defined.
	 */
    public boolean isOptimiseDefined()
	{
	    return isDefined(optimiseDef);
	}

    /**
     * Returns the value of the <code>public</code> attribute.
     *
     * @return the value of the <code>public</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getPublic()
        throws IllegalStateException
    {
        if(isDefined(publicDef))
        {
            return ((Boolean)get(publicDef)).booleanValue();
        }
        else
        {
            throw new IllegalStateException("value of attribute public is undefined"+
			    " for resource #"+getId());
        }
    }

    /**
     * Returns the value of the <code>public</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>public</code> attribute.
     */
    public boolean getPublic(boolean defaultValue)
    {
        if(isDefined(publicDef))
        {
            return ((Boolean)get(publicDef)).booleanValue();
        }
        else
        {
            return defaultValue;
        }
    }

    /**
     * Sets the value of the <code>public</code> attribute.
     *
     * @param value the value of the <code>public</code> attribute.
     */
    public void setPublic(boolean value)
    {
        try
        {
            set(publicDef, new Boolean(value));
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
     * Removes the value of the <code>public</code> attribute.
     */
    public void unsetPublic()
    {
        try
        {
            unset(publicDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>public</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>public</code> attribute is defined.
	 */
    public boolean isPublicDefined()
	{
	    return isDefined(publicDef);
	}
  
    // @custom methods ///////////////////////////////////////////////////////
    // @order filesLocation
    // TODO add filesLocation as first attribute in attrs order
}
