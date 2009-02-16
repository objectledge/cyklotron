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
 
package net.cyklotron.cms.documents;

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
 * An implementation of <code>documents.footer</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class FooterResourceImpl
    extends CmsNodeResourceImpl
    implements FooterResource
{
    // class variables /////////////////////////////////////////////////////////

    /** Class variables initialization status. */
    private static boolean definitionsInitialized;
	
    /** The AttributeDefinition object for the <code>content</code> attribute. */
    private static AttributeDefinition contentDef;

    /** The AttributeDefinition object for the <code>enabled</code> attribute. */
    private static AttributeDefinition enabledDef;

    /** The AttributeDefinition object for the <code>sequence</code> attribute. */
    private static AttributeDefinition sequenceDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>documents.footer</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     */
    public FooterResourceImpl()
    {
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>documents.footer</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static FooterResource getFooterResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof FooterResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not documents.footer");
        }
        return (FooterResource)res;
    }

    /**
     * Creates a new <code>documents.footer</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @return a new FooterResource instance.
     * @throws InvalidResourceNameException if the name argument contains illegal characters.
     */
    public static FooterResource createFooterResource(CoralSession session, String name,
        Resource parent)
        throws InvalidResourceNameException
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("documents.footer");
            Map attrs = new HashMap();
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof FooterResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (FooterResource)res;
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
     * Returns the value of the <code>content</code> attribute.
     *
     * @return the value of the <code>content</code> attribute.
     */
    public String getContent()
    {
        return (String)getInternal(contentDef, null);
    }
    
    /**
     * Returns the value of the <code>content</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>content</code> attribute.
     */
    public String getContent(String defaultValue)
    {
        return (String)getInternal(contentDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>content</code> attribute.
     *
     * @param value the value of the <code>content</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setContent(String value)
    {
        try
        {
            if(value != null)
            {
                set(contentDef, value);
            }
            else
            {
                unset(contentDef);
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
	 * Checks if the value of the <code>content</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>content</code> attribute is defined.
	 */
    public boolean isContentDefined()
	{
	    return isDefined(contentDef);
	}

    /**
     * Returns the value of the <code>enabled</code> attribute.
     *
     * @return the value of the <code>enabled</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getEnabled()
        throws IllegalStateException
    {
	    Boolean value = (Boolean)getInternal(enabledDef, null);
        if(value != null)
        {
            return value.booleanValue();
        }
        else
        {
            throw new IllegalStateException("value of attribute enabled is undefined"+
			    " for resource #"+getId());
        }
    }

    /**
     * Returns the value of the <code>enabled</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>enabled</code> attribute.
     */
    public boolean getEnabled(boolean defaultValue)
    {
		return ((Boolean)getInternal(enabledDef, new Boolean(defaultValue))).booleanValue();
	}

    /**
     * Sets the value of the <code>enabled</code> attribute.
     *
     * @param value the value of the <code>enabled</code> attribute.
     */
    public void setEnabled(boolean value)
    {
        try
        {
            set(enabledDef, new Boolean(value));
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
     * Removes the value of the <code>enabled</code> attribute.
     */
    public void unsetEnabled()
    {
        try
        {
            unset(enabledDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>enabled</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>enabled</code> attribute is defined.
	 */
    public boolean isEnabledDefined()
	{
	    return isDefined(enabledDef);
	}

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
	    Integer value = (Integer)getInternal(sequenceDef, null);
        if(value != null)
        {
            return value.intValue();
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
		return ((Integer)getInternal(sequenceDef, new Integer(defaultValue))).intValue();
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
  
    // @custom methods ///////////////////////////////////////////////////////
    
    
    public String getValue()
    {
        return getName();
    }
    
    public String getOptionName()
    {
        return getName().replace(" ","/");
    }
}
