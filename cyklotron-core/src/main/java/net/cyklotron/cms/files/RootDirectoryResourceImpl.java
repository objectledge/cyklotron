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

import java.util.HashMap;
import java.util.Map;

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

import org.jcontainer.dna.Logger;

/**
 * An implementation of <code>cms.files.root_directory</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class RootDirectoryResourceImpl
    extends DirectoryResourceImpl
    implements RootDirectoryResource
{
    // instance variables ////////////////////////////////////////////////////

    /** The AttributeDefinition object for the <code>external</code> attribute. */
    private AttributeDefinition externalDef;

    /** The AttributeDefinition object for the <code>rootPath</code> attribute. */
    private AttributeDefinition rootPathDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>cms.files.root_directory</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     * @param schema the CoralSchema.
     * @param database the Database.
     * @param logger the Logger.
     */
    public RootDirectoryResourceImpl(CoralSchema schema, Database database, Logger logger)
    {
        super(schema, database, logger);
        try
        {
            ResourceClass rc = schema.getResourceClass("cms.files.root_directory");
            externalDef = rc.getAttribute("external");
            rootPathDef = rc.getAttribute("rootPath");
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>cms.files.root_directory</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static RootDirectoryResource getRootDirectoryResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof RootDirectoryResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not cms.files.root_directory");
        }
        return (RootDirectoryResource)res;
    }

    /**
     * Creates a new <code>cms.files.root_directory</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @return a new RootDirectoryResource instance.
     */
    public static RootDirectoryResource createRootDirectoryResource(CoralSession session, String
        name, Resource parent)
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("cms.files.root_directory");
            Map attrs = new HashMap();
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof RootDirectoryResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (RootDirectoryResource)res;
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
     * Returns the value of the <code>external</code> attribute.
     *
     * @return the value of the <code>external</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getExternal()
        throws IllegalStateException
    {
        if(isDefined(externalDef))
        {
            return ((Boolean)get(externalDef)).booleanValue();
        }
        else
        {
            throw new IllegalStateException("value of attribute external is undefined"+
			    " for resource #"+getId());
        }
    }

    /**
     * Returns the value of the <code>external</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>external</code> attribute.
     */
    public boolean getExternal(boolean defaultValue)
    {
        if(isDefined(externalDef))
        {
            return ((Boolean)get(externalDef)).booleanValue();
        }
        else
        {
            return defaultValue;
        }
    }

    /**
     * Sets the value of the <code>external</code> attribute.
     *
     * @param value the value of the <code>external</code> attribute.
     */
    public void setExternal(boolean value)
    {
        try
        {
            set(externalDef, new Boolean(value));
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
     * Removes the value of the <code>external</code> attribute.
     */
    public void unsetExternal()
    {
        try
        {
            unset(externalDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>external</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>external</code> attribute is defined.
	 */
    public boolean isExternalDefined()
	{
	    return isDefined(externalDef);
	}
 
    /**
     * Returns the value of the <code>rootPath</code> attribute.
     *
     * @return the value of the <code>rootPath</code> attribute.
     */
    public String getRootPath()
    {
        return (String)get(rootPathDef);
    }
    
    /**
     * Returns the value of the <code>rootPath</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>rootPath</code> attribute.
     */
    public String getRootPath(String defaultValue)
    {
        if(isDefined(rootPathDef))
        {
            return (String)get(rootPathDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>rootPath</code> attribute.
     *
     * @param value the value of the <code>rootPath</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setRootPath(String value)
    {
        try
        {
            if(value != null)
            {
                set(rootPathDef, value);
            }
            else
            {
                unset(rootPathDef);
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
	 * Checks if the value of the <code>rootPath</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>rootPath</code> attribute is defined.
	 */
    public boolean isRootPathDefined()
	{
	    return isDefined(rootPathDef);
	}
  
    // @custom methods ///////////////////////////////////////////////////////
    // @extends cms.files.directory
}
